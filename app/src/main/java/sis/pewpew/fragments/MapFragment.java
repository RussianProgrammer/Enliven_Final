package sis.pewpew.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import sis.pewpew.MainActivity;
import sis.pewpew.R;
import sis.pewpew.additions.MarkerInfoActivity;

import static android.content.Context.LOCATION_SERVICE;

public class MapFragment extends Fragment {

    private MapView mMapView;
    private boolean mLocationPermissionGranted;
    private GoogleMap mMap;
    private boolean addPointsDialogShown = false;
    private FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    private DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
    private boolean closed;
    private ProgressDialog mProgressDialog;

    private Locale locale = new Locale("ru");
    private String date = new SimpleDateFormat("dd-MM-yyyy", locale).format(new Date());

    private LatLng mDefaultLocation;
    private LatLng mMoscow = new LatLng(55.755826, 37.6173);
    private LatLng mArkhangelsk = new LatLng(64.547251, 40.560155);
    private LatLng mVladivostok = new LatLng(43.119809, 131.886924);
    private LatLng mEkaterinburg = new LatLng(56.838926, 60.605703);
    private LatLng mOmsk = new LatLng(54.988480, 73.324236);

    private List<Marker> usualMarkers = new ArrayList<>();
    private List<Marker> eventMarkers = new ArrayList<>();

    private MarkerOptions batteryMarkerOptions = new MarkerOptions();
    private MarkerOptions paperMarkerOptions = new MarkerOptions();
    private MarkerOptions glassMarkerOptions = new MarkerOptions();
    private MarkerOptions metalMarkerOptions = new MarkerOptions();
    private MarkerOptions plasticMarkerOptions = new MarkerOptions();
    private MarkerOptions bulbMarkerOptions = new MarkerOptions();
    private MarkerOptions dangersMarkerOptions = new MarkerOptions();
    private MarkerOptions otherMarkerOptions = new MarkerOptions();
    private MarkerOptions eventMarkerOptions = new MarkerOptions();

    private String markerIdentifier;
    private String markerTitle;
    private String markerGroup;
    private double markerLatitude;
    private double markerLongitude;

    LocationListener mLocationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {

            if (!closed) {

                if (ContextCompat.checkSelfPermission(getActivity(),
                        android.Manifest.permission.ACCESS_FINE_LOCATION)
                        == PackageManager.PERMISSION_GRANTED) {
                    mLocationPermissionGranted = true;
                } else {
                    int PERMISSION_REQUEST_CODE = 5;
                    ActivityCompat.requestPermissions(getActivity(),
                            new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                            PERMISSION_REQUEST_CODE);
                }

                if (!user.isAnonymous()) {
                    mMap.setMyLocationEnabled(true);
                    mMap.getUiSettings().setMyLocationButtonEnabled(true);
                }

                for (final Marker marker : usualMarkers) {
                    Location loc = new Location(location);
                    loc.setLatitude(marker.getPosition().latitude);
                    loc.setLongitude(marker.getPosition().longitude);

                    if (location.distanceTo(loc) < 50) {
                        addUsualPoints(marker, 0);
                    }
                }

                for (final Marker marker : eventMarkers) {
                    Location loc = new Location(location);
                    loc.setLatitude(marker.getPosition().latitude);
                    loc.setLongitude(marker.getPosition().longitude);

                    if (location.distanceTo(loc) < 50) {
                        addEventPoints(marker, 0);
                    }
                }
            }
        }

        @Override
        public void onStatusChanged(String s, int i, Bundle bundle) {
        }

        @Override
        public void onProviderEnabled(String s) {
        }

        @Override
        public void onProviderDisabled(String s) {
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        SharedPreferences settings = getActivity().getSharedPreferences("MAP", 0);
        boolean dialogShown = settings.getBoolean("dialogShown", false);

        ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.child("users").child(user.getUid()).child("points").getValue() == null && !closed) {
                    mDatabase.child("users").child(user.getUid()).child("points").setValue(0);
                }
                if (dataSnapshot.child("users").child(user.getUid()).child("timesUsed").getValue() == null && !closed) {
                    mDatabase.child("users").child(user.getUid()).child("timesUsed").setValue(0);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w("TAG", "loadPost:onCancelled", databaseError.toException());
            }
        };
        mDatabase.addValueEventListener(postListener);

        if (!dialogShown) {
            AlertDialog.Builder mapFragmentWelcomeDialog = new AlertDialog.Builder(getActivity());
            mapFragmentWelcomeDialog.setTitle(getString(R.string.map_fragment_name));
            mapFragmentWelcomeDialog.setCancelable(false);
            mapFragmentWelcomeDialog.setIcon(R.drawable.ic_menu_map);
            mapFragmentWelcomeDialog.setMessage("В разделе \"Карта\" Вы сможете увидеть все доступные экопункты в Вашем городе. Коснувшись любого флажка, " +
                    "Вы сможете просмотреть подробную информацию о нем, а также проложить к нему маршрут. Кроме того, не забудьте открыть приложение, " +
                    "когда решите посетить один из них. Как только Вы окажетесь в зоне флажка, Вам будут начислены специальные очки, " +
                    "которые будут отображаться в Вашем профиле. Не забывайте касаться информационных окон флажков, чтобы узнать о них больше.");
            mapFragmentWelcomeDialog.setNegativeButton("Понятно", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.cancel();
                    if (user.isAnonymous()) {
                        AlertDialog.Builder mapFragmentAnonymousDialog = new AlertDialog.Builder(getActivity());
                        mapFragmentAnonymousDialog.setTitle("Карта в деморежиме");
                        mapFragmentAnonymousDialog.setCancelable(false);
                        mapFragmentAnonymousDialog.setIcon(R.drawable.ic_error_demo);
                        mapFragmentAnonymousDialog.setMessage("В демонстрационном режиме приложение не получает Ваше реальное " +
                                "местоположение. Вместо этого на Карте появляется большой флажок Вашего виртуального " +
                                "местонахождения, который по умолчанию находится в центре города. С помощью долгого касания, " +
                                "Вы можете перемещать его в любую точку карты. Чтобы активировать экопункт, " +
                                "флажок местоположения должен находиться в радиусе 50 метров от флажка экопункта.");
                        mapFragmentAnonymousDialog.setNegativeButton("Понятно", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.cancel();
                            }
                        });
                        mapFragmentAnonymousDialog.show();
                    }
                }
            });
            mapFragmentWelcomeDialog.show();

            SharedPreferences.Editor editor = settings.edit();
            editor.putBoolean("dialogShown", true);
            editor.apply();
        }

        View rootView = inflater.inflate(R.layout.fragment_map, container, false);
        ((MainActivity) getActivity()).setActionBarTitle(getString(R.string.map_fragment_name));

        SharedPreferences pref = PreferenceManager
                .getDefaultSharedPreferences(getActivity());
        final String selectedCity = pref.getString("city_selector", "");

        mMapView = (MapView) rootView.findViewById(R.id.mapView);
        mMapView.onCreate(savedInstanceState);
        mMapView.onResume();

        try {
            MapsInitializer.initialize(getActivity().getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }

        mMapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap mMap) {

                switch (selectedCity) {
                    case "1":
                        mDefaultLocation = mMoscow;
                        break;
                    case "2":
                        mDefaultLocation = mArkhangelsk;
                        break;
                    case "3":
                        mDefaultLocation = mEkaterinburg;
                        break;
                    case "4":
                        mDefaultLocation = mOmsk;
                        break;
                    case "5":
                        mDefaultLocation = mVladivostok;
                        break;
                }

                if (mDefaultLocation == null) {
                    mDefaultLocation = mMoscow;
                }

                if (user.isAnonymous()) {
                    mMap.setOnMarkerDragListener(new GoogleMap.OnMarkerDragListener() {
                        @Override
                        public void onMarkerDragStart(Marker marker) {
                            //IGNORE ACTION
                        }

                        @Override
                        public void onMarkerDrag(Marker marker) {
                            //IGNORE ACTION
                        }

                        @Override
                        public void onMarkerDragEnd(Marker marker) {
                            for (final Marker markers : usualMarkers) {
                                float[] results = new float[1];
                                Location.distanceBetween(
                                        markers.getPosition().latitude,
                                        markers.getPosition().longitude,
                                        marker.getPosition().latitude,
                                        marker.getPosition().longitude,
                                        results);
                                if (results[0] < 50) {
                                    addUsualPoints(markers, 5);
                                }
                            }

                            for (final Marker markers : eventMarkers) {
                                float[] results = new float[1];
                                Location.distanceBetween(
                                        markers.getPosition().latitude,
                                        markers.getPosition().longitude,
                                        marker.getPosition().latitude,
                                        marker.getPosition().longitude,
                                        results);
                                if (results[0] < 50) {
                                    addEventPoints(markers, 5);
                                }
                            }
                        }
                    });

                    mMap.addMarker(new MarkerOptions().position(mDefaultLocation)
                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.my_location_marker_icon))
                            .draggable(true)).hideInfoWindow();
                }

                MapFragment.this.mMap = mMap;
                mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
                    @Override
                    public void onInfoWindowClick(Marker marker) {
                        Intent intent = new Intent(getActivity().getApplicationContext(), MarkerInfoActivity.class);
                        intent.putExtra("TITLE", marker.getTitle());
                        intent.putExtra("SNIPPET", marker.getSnippet());
                        startActivity(intent);

                    }
                });

                if (!closed) {

                    if (ContextCompat.checkSelfPermission(getActivity().getApplicationContext(),
                            android.Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {
                        mLocationPermissionGranted = true;
                    } else {
                        int PERMISSION_REQUEST_CODE = 5;
                        ActivityCompat.requestPermissions(getActivity(),
                                new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                                PERMISSION_REQUEST_CODE);
                    }

                    mMap.getUiSettings().setZoomControlsEnabled(true);
                    mMap.setMinZoomPreference(10.0f);
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mDefaultLocation, 12));

                    if (mLocationPermissionGranted) {

                        setMarkerOptionsIcon();
                        addMarkers();

                        if (!user.isAnonymous()) {
                            LocationManager mLocationManager = (LocationManager) getActivity().getSystemService(LOCATION_SERVICE);
                            Location mLastKnownLocation = mLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                            mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0,
                                    0, mLocationListener, null);
                            if (mLastKnownLocation != null) {
                                mLocationListener.onLocationChanged(mLastKnownLocation);
                            }
                        }
                    }
                }
            }
        });
        return rootView;
    }

    private void setMarkerOptionsIcon() {
        batteryMarkerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.battery_marker_icon));
        paperMarkerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.paper_marker_icon));
        glassMarkerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.glass_marker_icon));
        metalMarkerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.metal_marker_icon));
        plasticMarkerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.plastic_marker_icon));
        bulbMarkerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.bulb_marker_icon));
        dangersMarkerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.dangers_marker_icon));
        otherMarkerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.another_marker_icon));
        eventMarkerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.event_marker_icon));
    }

    private void addMarkers() {
        ValueEventListener markersListener = new ValueEventListener() {
            @SuppressWarnings("ConstantConditions")
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.child("users").child(user.getUid()).child("timesUsed").getValue() == null) {
                    mDatabase.child("users").child(user.getUid()).child("timesUsed").setValue(0);
                }

                for (DataSnapshot childSnapShot : dataSnapshot.child("markers").getChildren()) {
                    if (childSnapShot.child("id").getValue() != null) {
                        markerIdentifier = childSnapShot.child("id").getValue().toString();
                    } else {
                        markerIdentifier = "unknown";
                    }
                    if (childSnapShot.child("title").getValue() != null) {
                        markerTitle = childSnapShot.child("title").getValue().toString();
                    } else {
                        markerTitle = "Неизвестный пункт";
                    }
                    if (childSnapShot.child("group").getValue() != null) {
                        markerGroup = childSnapShot.child("group").getValue().toString();
                    } else {
                        markerGroup = "battery";
                    }
                    if (childSnapShot.child("lat").getValue() != null) {
                        markerLatitude = (double) childSnapShot.child("lat").getValue();
                    } else {
                        markerLatitude = 5.0000;
                    }
                    if (childSnapShot.child("long").getValue() != null) {
                        markerLongitude = (double) childSnapShot.child("long").getValue();
                    } else if (childSnapShot.child("lng").getValue() != null) {
                        markerLongitude = (double) childSnapShot.child("lng").getValue();
                    } else {
                        markerLongitude = 7.0000;
                    }

                    switch (markerGroup) {
                        case "battery":
                            usualMarkers.add(mMap.addMarker(batteryMarkerOptions
                                    .position(new LatLng(markerLatitude, markerLongitude))
                                    .title(markerTitle).snippet(markerIdentifier)));
                            break;
                        case "paper":
                            usualMarkers.add(mMap.addMarker(paperMarkerOptions
                                    .position(new LatLng(markerLatitude, markerLongitude))
                                    .title(markerTitle).snippet(markerIdentifier)));
                            break;
                        case "glass":
                            usualMarkers.add(mMap.addMarker(glassMarkerOptions
                                    .position(new LatLng(markerLatitude, markerLongitude))
                                    .title(markerTitle).snippet(markerIdentifier)));
                            break;
                        case "metal":
                            usualMarkers.add(mMap.addMarker(metalMarkerOptions
                                    .position(new LatLng(markerLatitude, markerLongitude))
                                    .title(markerTitle).snippet(markerIdentifier)));
                            break;
                        case "plastic":
                            usualMarkers.add(mMap.addMarker(plasticMarkerOptions
                                    .position(new LatLng(markerLatitude, markerLongitude))
                                    .title(markerTitle).snippet(markerIdentifier)));
                            break;
                        case "bulb":
                            usualMarkers.add(mMap.addMarker(bulbMarkerOptions
                                    .position(new LatLng(markerLatitude, markerLongitude))
                                    .title(markerTitle).snippet(markerIdentifier)));
                            break;
                        case "danger":
                            usualMarkers.add(mMap.addMarker(dangersMarkerOptions
                                    .position(new LatLng(markerLatitude, markerLongitude))
                                    .title(markerTitle).snippet(markerIdentifier)));
                            break;
                        case "other":
                            usualMarkers.add(mMap.addMarker(otherMarkerOptions
                                    .position(new LatLng(markerLatitude, markerLongitude))
                                    .title(markerTitle).snippet(markerIdentifier)));
                            break;
                        case "event":
                            eventMarkers.add(mMap.addMarker(eventMarkerOptions
                                    .position(new LatLng(markerLatitude, markerLongitude))
                                    .title(markerTitle).snippet(markerIdentifier)));
                            break;
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w("TAG", "loadPost:onCancelled", databaseError.toException());
            }
        };
        mDatabase.addValueEventListener(markersListener);
    }

    private void showGratitudeDialog() {
        final AlertDialog.Builder gratitudeDialog = new AlertDialog.Builder(getActivity());
        gratitudeDialog.setTitle("Спасибо");
        gratitudeDialog.setMessage("Благодаря Вам мир стал лучше. Очки успешно добавлены к Вашему профилю.");
        gratitudeDialog.setNegativeButton("Закрыть", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
                addPointsDialogShown = false;
            }
        });
        gratitudeDialog.setPositiveButton("Поделиться", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                share();
            }
        });
        gratitudeDialog.show();
    }

    private void share() {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        String shareBody = "Найдя и использовав экопункт в приложении Enliven, я показал, " +
                "насколько мне небезразлично будущее нашей планеты. " +
                "Присоединяйтесь ко мне, пора все менять! #Enliven https://play.google.com/apps/testing/sis.pewpew";
        shareIntent.putExtra(Intent.EXTRA_TEXT, shareBody);
        startActivity(Intent.createChooser(shareIntent, "Поделиться профилем"));
    }

    public void showProgressDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(getActivity());
            mProgressDialog.setTitle("Получение очков…");
            mProgressDialog.setMessage("Подождите…");
            mProgressDialog.setIndeterminate(true);
        }
        mProgressDialog.show();
    }

    public void hideProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
    }

    private void addEventPoints(final Marker marker, int flag) {
        final AlertDialog.Builder isEventPointUsedDialog = new AlertDialog.Builder(getActivity());
        isEventPointUsedDialog.setTitle("Обнаружен флажок экособытия");
        isEventPointUsedDialog.setIcon(R.drawable.event_marker_icon);
        isEventPointUsedDialog.setCancelable(false);
        isEventPointUsedDialog.setMessage("Мы рады приветствовать Вас на экособытии \"" + marker.getTitle() + "\"! " +
                "Если это не случайность, и Вы действительно посетили это мероприятие, " +
                "мы наградим Вас заслуженными очками.");
        isEventPointUsedDialog.setNegativeButton("Закрыть", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
                addPointsDialogShown = false;
            }
        });
        isEventPointUsedDialog.setPositiveButton("Получить", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                showProgressDialog();

                if (!user.isAnonymous()) {

                    mDatabase.child("users").child(user.getUid()).child("markers").child(marker.getSnippet()).child(date).setValue("used");

                    DatabaseReference mProfilePoints = FirebaseDatabase.getInstance().getReference()
                            .child("users").child(user.getUid()).child("points");
                    DatabaseReference mPublicPoints = FirebaseDatabase.getInstance().getReference()
                            .child("progress").child("points");
                    DatabaseReference mTimesUsed = FirebaseDatabase.getInstance().getReference()
                            .child("markers").child(marker.getSnippet()).child("timesUsed");
                    DatabaseReference mTimesUsedProfile = FirebaseDatabase.getInstance().getReference()
                            .child("users").child(user.getUid()).child("timesUsed");
                    DatabaseReference mTimesUsedProgress = FirebaseDatabase.getInstance().getReference()
                            .child("progress").child("timesUsed");
                    onTimesUsedCount(mTimesUsed);
                    onTimesUsedProfileCount(mTimesUsedProfile);
                    onTimesUsedProgressCount(mTimesUsedProgress);
                    onEventProfilePointsAdded(mProfilePoints);
                    onEventPublicPointsAdded(mPublicPoints);

                    SharedPreferences sp = getActivity().getSharedPreferences("TIME", Activity.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sp.edit();
                    editor.putLong("timeMillis for" + marker.getSnippet(), System.currentTimeMillis());
                    editor.apply();

                } else {
                    onEventProfilePointsAdded(FirebaseDatabase.getInstance().getReference()
                            .child("users").child(user.getUid()).child("points"));
                    onTimesUsedProfileCount(FirebaseDatabase.getInstance().getReference()
                            .child("users").child(user.getUid()).child("timesUsed"));
                }
            }
        });

        SharedPreferences sp = getActivity().getSharedPreferences("TIME", Activity.MODE_PRIVATE);
        long difference = sp.getLong("timeMillis for" + marker.getSnippet(), -1);

        if ((System.currentTimeMillis() - difference > 43200000 || flag != 0) && !addPointsDialogShown) {
            isEventPointUsedDialog.show();
            addPointsDialogShown = true;
        }
    }

    private void addUsualPoints(final Marker marker, int flag) {
        final AlertDialog.Builder isPointUsedDialog = new AlertDialog.Builder(getActivity());
        isPointUsedDialog.setTitle("Обнаружен флажок экопункта");
        isPointUsedDialog.setIcon(R.drawable.ic_menu_marker_icon);
        isPointUsedDialog.setCancelable(false);
        isPointUsedDialog.setMessage("Поздравляем, Вы нашли экопункт \"" + marker.getTitle() + "\"! " +
                "Если это не случайность, и Вы действительно использовали этот пункт по назначению, " +
                "мы подарим Вам заслуженные очки.");
        isPointUsedDialog.setNegativeButton("Закрыть", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
                addPointsDialogShown = false;
            }
        });
        isPointUsedDialog.setPositiveButton("Получить", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                showProgressDialog();

                if (!user.isAnonymous()) {

                    mDatabase.child("users").child(user.getUid()).child("markers").child(marker.getSnippet()).child(date).setValue("used");

                    DatabaseReference mProfilePoints = FirebaseDatabase.getInstance().getReference()
                            .child("users").child(user.getUid()).child("points");
                    DatabaseReference mPublicPoints = FirebaseDatabase.getInstance().getReference()
                            .child("progress").child("points");
                    DatabaseReference mTimesUsed = FirebaseDatabase.getInstance().getReference()
                            .child("markers").child(marker.getSnippet()).child("timesUsed");
                    DatabaseReference mTimesUsedProfile = FirebaseDatabase.getInstance().getReference()
                            .child("users").child(user.getUid()).child("timesUsed");
                    DatabaseReference mTimesUsedProgress = FirebaseDatabase.getInstance().getReference()
                            .child("progress").child("timesUsed");
                    onTimesUsedCount(mTimesUsed);
                    onTimesUsedProfileCount(mTimesUsedProfile);
                    onTimesUsedProgressCount(mTimesUsedProgress);
                    onUsualProfilePointsAdded(mProfilePoints);
                    onUsualPublicPointsAdded(mPublicPoints);

                    SharedPreferences sp = getActivity().getSharedPreferences("TIME", Activity.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sp.edit();
                    editor.putLong("timeMillis for" + marker.getSnippet(), System.currentTimeMillis());
                    editor.apply();

                } else {
                    onUsualProfilePointsAdded(FirebaseDatabase.getInstance().getReference()
                            .child("users").child(user.getUid()).child("points"));
                    onTimesUsedProfileCount(FirebaseDatabase.getInstance().getReference()
                            .child("users").child(user.getUid()).child("timesUsed"));
                }
            }
        });

        SharedPreferences sp = getActivity().getSharedPreferences("TIME", Activity.MODE_PRIVATE);
        long difference = sp.getLong("timeMillis for" + marker.getSnippet(), -1);

        if ((System.currentTimeMillis() - difference > 3600000 || flag != 0) && !addPointsDialogShown) {
            isPointUsedDialog.show();
            addPointsDialogShown = true;
        }
    }

    private void onUsualProfilePointsAdded(DatabaseReference postRef) {
        postRef.runTransaction(new Transaction.Handler() {
            @Override
            public Transaction.Result doTransaction(MutableData mutableData) {
                long pointsFromDatabase = 0;
                if (mutableData != null) {
                    pointsFromDatabase = (long) mutableData.getValue();
                }
                pointsFromDatabase = pointsFromDatabase + 200;
                assert mutableData != null;
                mutableData.setValue(pointsFromDatabase);
                return Transaction.success(mutableData);
            }

            @Override
            public void onComplete(DatabaseError databaseError, boolean b,
                                   DataSnapshot dataSnapshot) {
                hideProgressDialog();
                showGratitudeDialog();
                Log.d("TAG", "postTransaction:onComplete:" + databaseError);
            }
        });
    }

    private void onTimesUsedCount(DatabaseReference postRef) {
        postRef.runTransaction(new Transaction.Handler() {
            @Override
            public Transaction.Result doTransaction(MutableData mutableData) {
                long timesUsed = 0;
                if (mutableData != null) {
                    timesUsed = (long) mutableData.getValue();
                }
                timesUsed = timesUsed + 1;
                assert mutableData != null;
                mutableData.setValue(timesUsed);
                return Transaction.success(mutableData);
            }

            @Override
            public void onComplete(DatabaseError databaseError, boolean b,
                                   DataSnapshot dataSnapshot) {
                Log.d("TAG", "postTransaction:onComplete:" + databaseError);
            }
        });
    }

    private void onTimesUsedProfileCount(DatabaseReference postRef) {
        postRef.runTransaction(new Transaction.Handler() {
            @Override
            public Transaction.Result doTransaction(MutableData mutableData) {
                long timesUsed = 0;
                if (mutableData != null) {
                    timesUsed = (long) mutableData.getValue();
                }
                timesUsed = timesUsed + 1;
                assert mutableData != null;
                mutableData.setValue(timesUsed);
                return Transaction.success(mutableData);
            }

            @Override
            public void onComplete(DatabaseError databaseError, boolean b,
                                   DataSnapshot dataSnapshot) {
                Log.d("TAG", "postTransaction:onComplete:" + databaseError);
            }
        });
    }

    private void onTimesUsedProgressCount(DatabaseReference postRef) {
        postRef.runTransaction(new Transaction.Handler() {
            @Override
            public Transaction.Result doTransaction(MutableData mutableData) {
                long timesUsed = 0;
                if (mutableData != null) {
                    timesUsed = (long) mutableData.getValue();
                }
                timesUsed = timesUsed + 1;
                assert mutableData != null;
                mutableData.setValue(timesUsed);
                return Transaction.success(mutableData);
            }

            @Override
            public void onComplete(DatabaseError databaseError, boolean b,
                                   DataSnapshot dataSnapshot) {
                Log.d("TAG", "postTransaction:onComplete:" + databaseError);
            }
        });
    }

    private void onUsualPublicPointsAdded(DatabaseReference postRef) {
        postRef.runTransaction(new Transaction.Handler() {
            @Override
            public Transaction.Result doTransaction(MutableData mutableData) {
                long pointsFromDatabase = 0;
                if (mutableData != null) {
                    pointsFromDatabase = (long) mutableData.getValue();
                }
                pointsFromDatabase = pointsFromDatabase + 200;
                assert mutableData != null;
                mutableData.setValue(pointsFromDatabase);
                return Transaction.success(mutableData);
            }

            @Override
            public void onComplete(DatabaseError databaseError, boolean b,
                                   DataSnapshot dataSnapshot) {
                Log.d("TAG", "postTransaction:onComplete:" + databaseError);
            }
        });
    }

    private void onEventProfilePointsAdded(DatabaseReference postRef) {
        postRef.runTransaction(new Transaction.Handler() {
            @Override
            public Transaction.Result doTransaction(MutableData mutableData) {
                long pointsFromDatabase = 0;
                if (mutableData != null) {
                    pointsFromDatabase = (long) mutableData.getValue();
                }
                pointsFromDatabase = pointsFromDatabase + 1000;
                assert mutableData != null;
                mutableData.setValue(pointsFromDatabase);
                return Transaction.success(mutableData);
            }

            @Override
            public void onComplete(DatabaseError databaseError, boolean b,
                                   DataSnapshot dataSnapshot) {
                hideProgressDialog();
                showGratitudeDialog();
                Log.d("TAG", "postTransaction:onComplete:" + databaseError);
            }
        });
    }

    private void onEventPublicPointsAdded(DatabaseReference postRef) {
        postRef.runTransaction(new Transaction.Handler() {
            @Override
            public Transaction.Result doTransaction(MutableData mutableData) {
                long pointsFromDatabase = 0;
                if (mutableData != null) {
                    pointsFromDatabase = (long) mutableData.getValue();
                }
                pointsFromDatabase = pointsFromDatabase + 1000;
                assert mutableData != null;
                mutableData.setValue(pointsFromDatabase);
                return Transaction.success(mutableData);
            }

            @Override
            public void onComplete(DatabaseError databaseError, boolean b,
                                   DataSnapshot dataSnapshot) {
                Log.d("TAG", "postTransaction:onComplete:" + databaseError);
            }
        });
    }

    @Override
    public void onStop() {
        super.onStop();
        hideProgressDialog();
    }

    @Override
    public void onStart() {
        super.onStart();
        mMapView.onStart();
    }

    @Override
    public void onPause() {
        super.onPause();
        mMapView.onPause();
        closed = true;
        hideProgressDialog();
    }

    @Override
    public void onResume() {
        super.onResume();
        mMapView.onResume();
        closed = false;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
        hideProgressDialog();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mMapView.onLowMemory();
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

}