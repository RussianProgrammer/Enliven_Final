package sis.pewpew.fragments;

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

import java.util.ArrayList;
import java.util.List;

import sis.pewpew.MainActivity;
import sis.pewpew.R;
import sis.pewpew.utils.MarkerInfoActivity;

import static android.content.Context.LOCATION_SERVICE;
import static com.google.android.gms.internal.zzt.TAG;

public class MapFragment extends Fragment {

    private MapView mMapView;
    private LatLng mDefaultLocation = new LatLng(55.755826, 37.6173);
    private boolean mLocationPermissionGranted;
    private GoogleMap mMap;
    private boolean dialogShown = false;
    private FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    private DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
    private boolean closed;
    private ProgressDialog mProgressDialog;

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

                mMap.setMyLocationEnabled(true);
                mMap.getUiSettings().setMyLocationButtonEnabled(true);
                mMap.getUiSettings().setZoomControlsEnabled(true);

                for (final Marker marker : usualMarkers) {
                    Location loc = new Location(location);
                    loc.setLatitude(marker.getPosition().latitude);
                    loc.setLongitude(marker.getPosition().longitude);

                    if (location.distanceTo(loc) < 50) {

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
                            }
                        });
                        isPointUsedDialog.setPositiveButton("Получить", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
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
                                showProgressDialog();
                            }
                        });

                        if (!dialogShown) {
                            isPointUsedDialog.show();
                            dialogShown = true;
                        }
                    }
                }

                for (final Marker marker : eventMarkers) {
                    Location loc = new Location(location);
                    loc.setLatitude(marker.getPosition().latitude);
                    loc.setLongitude(marker.getPosition().longitude);

                    if (location.distanceTo(loc) < 50) {

                        final AlertDialog.Builder isPointUsedDialog = new AlertDialog.Builder(getActivity());
                        isPointUsedDialog.setTitle("Обнаружен флажок экособытия");
                        isPointUsedDialog.setIcon(R.drawable.event_marker_icon);
                        isPointUsedDialog.setCancelable(false);
                        isPointUsedDialog.setMessage("Мы рады приветствовать Вас на экособытии \"" + marker.getTitle() + "\"! " +
                                "Если это не случайность, и Вы действительно посетили это мероприятие, " +
                                "мы наградим Вас заслуженными очками.");
                        isPointUsedDialog.setNegativeButton("Закрыть", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.cancel();
                            }
                        });
                        isPointUsedDialog.setPositiveButton("Получить", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
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
                                showProgressDialog();
                            }
                        });

                        if (!dialogShown) {
                            isPointUsedDialog.show();
                            dialogShown = true;
                        }
                    }
                }
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
                    Log.d(TAG, "postTransaction:onComplete:" + databaseError);
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
                    Log.d(TAG, "postTransaction:onComplete:" + databaseError);
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
                    Log.d(TAG, "postTransaction:onComplete:" + databaseError);
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
                    Log.d(TAG, "postTransaction:onComplete:" + databaseError);
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
                    Log.d(TAG, "postTransaction:onComplete:" + databaseError);
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
                    Log.d(TAG, "postTransaction:onComplete:" + databaseError);
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
                    Log.d(TAG, "postTransaction:onComplete:" + databaseError);
                }
            });
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
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
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
                    "которые будут отображаться в Вашем профиле.");
            mapFragmentWelcomeDialog.setNegativeButton("Понятно", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.cancel();
                }
            });
            mapFragmentWelcomeDialog.show();

            SharedPreferences.Editor editor = settings.edit();
            editor.putBoolean("dialogShown", true);
            editor.apply();
        }

        View rootView = inflater.inflate(R.layout.fragment_map, container, false);
        ((MainActivity) getActivity()).setActionBarTitle(getString(R.string.map_fragment_name));

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

                MapFragment.this.mMap = mMap;
                mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
                    @Override
                    public void onInfoWindowClick(Marker marker) {
                        if (marker.getSnippet().contains("ev")) {
                            Intent intent = new Intent(getActivity().getApplicationContext(), MarkerInfoActivity.class);
                            intent.putExtra("TITLE", marker.getTitle());
                            intent.putExtra("SNIPPET", marker.getSnippet());
                            startActivity(intent);
                        } else {
                            Intent intent = new Intent(getActivity().getApplicationContext(), MarkerInfoActivity.class);
                            intent.putExtra("TITLE", marker.getTitle());
                            intent.putExtra("SNIPPET", marker.getSnippet());
                            startActivity(intent);
                        }
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

                    mMap.setMinZoomPreference(10.0f);
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mDefaultLocation, 12));

                    if (mLocationPermissionGranted) {

                        setMarkerOptionsIcon();
                        addMarkers();

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
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
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
                    } else {
                        markerLongitude = 7.0000;
                    }
                    if (childSnapShot.child("eventMark").getValue() == null) {
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
                        }
                    } else {
                        eventMarkers.add(mMap.addMarker(eventMarkerOptions
                                .position(new LatLng(markerLatitude, markerLongitude))
                                .title(markerTitle).snippet(markerIdentifier)));
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
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
                "Присоединяйтесь ко мне, пора все менять! #Enliven";
        shareIntent.putExtra(Intent.EXTRA_TEXT, shareBody);
        startActivity(Intent.createChooser(shareIntent, "Поделиться профилем"));
    }

    public void showProgressDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(getActivity());
            mProgressDialog.setTitle("Зачисление…");
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