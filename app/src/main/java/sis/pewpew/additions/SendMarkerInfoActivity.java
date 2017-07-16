package sis.pewpew.additions;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import sis.pewpew.R;
import sis.pewpew.classes.MarkerDataUpload;

public class SendMarkerInfoActivity extends AppCompatActivity {

    private StorageReference mStorageRef;
    private DatabaseReference mDatabase;
    private ImageView imageView;
    private EditText editTitle;
    private EditText editAddress;
    private EditText editDetails;
    private EditText editWorkTime;
    private EditText editWorkTimeBreak;
    private EditText editContactsPhone;
    private EditText editContactsEmail;
    private EditText editContactsUrl;
    private EditText editLat1;
    private EditText editLat2;
    private EditText editLong1;
    private EditText editLong2;

    private int groupChoice = 0;
    private String finalId;
    private FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    public static final String STORAGE_PATH = "image/";
    public static final String DATABASE_PATH = "requests";
    private Uri imageUri;
    public static final int REQUEST_CODE = 5;
    private final List<EditText> necessaryFields = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_marker_info);

        mStorageRef = FirebaseStorage.getInstance().getReference();
        mDatabase = FirebaseDatabase.getInstance().getReference(DATABASE_PATH);

        imageView = (ImageView) findViewById(R.id.uploaded_image);
        editTitle = (EditText) findViewById(R.id.console_enter_title);
        editAddress = (EditText) findViewById(R.id.console_enter_address);
        editDetails = (EditText) findViewById(R.id.console_enter_details);
        editWorkTime = (EditText) findViewById(R.id.console_enter_work_time);
        editWorkTimeBreak = (EditText) findViewById(R.id.console_enter_work_time_break);
        editContactsPhone = (EditText) findViewById(R.id.console_enter_contacts_phone);
        editContactsEmail = (EditText) findViewById(R.id.console_enter_contacts_email);
        editContactsUrl = (EditText) findViewById(R.id.console_enter_contacts_url);
        editLat1 = (EditText) findViewById(R.id.console_enter_latitude_1);
        editLat2 = (EditText) findViewById(R.id.console_enter_latitude_2);
        editLong1 = (EditText) findViewById(R.id.console_enter_longitude_1);
        editLong2 = (EditText) findViewById(R.id.console_enter_longitude_2);

        Button chooseMarkerGroupButton = (Button) findViewById(R.id.choose_marker_group_button);

        necessaryFields.add(editTitle);
        necessaryFields.add(editAddress);
        necessaryFields.add(editDetails);
        necessaryFields.add(editContactsPhone);
        necessaryFields.add(editContactsEmail);
        necessaryFields.add(editContactsUrl);
        necessaryFields.add(editLat1);
        necessaryFields.add(editLat2);
        necessaryFields.add(editLong1);
        necessaryFields.add(editLong2);

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Выберите изображение"), REQUEST_CODE);
            }
        });

        chooseMarkerGroupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final AlertDialog.Builder builder = new AlertDialog.Builder(SendMarkerInfoActivity.this);
                builder.setTitle("Выберите группу, к которой пренадлежит Ваш экопункт:");
                builder.setSingleChoiceItems(R.array.listMarkerGroups, 0, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //Toast.makeText(SendMarkerInfoActivity.this, "" + i, Toast.LENGTH_LONG).show();
                        groupChoice = i;
                    }
                });
                builder.setPositiveButton("Продолжить", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        showSummary();
                    }
                });
                if (validateForm()) {
                    builder.show();
                }
            }

        });
    }

    private boolean validateForm() {
        boolean valid = true;
        for (EditText editText : necessaryFields) {
            if (editText.getText().toString().isEmpty()) {
                editText.setError("Поле должно быть заполнено");
                valid = false;
            } else {
                editAddress.setError(null);
            }
        }
        return valid;
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();

            try {
                Bitmap bm = Bitmap.createScaledBitmap(MediaStore.Images.Media.
                        getBitmap(getContentResolver(), imageUri), 720, 480, true);
                imageView.setImageBitmap(bm);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public String getImageExt(Uri uri) {
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    @SuppressWarnings("VisibleForTests")
    public void upload() {
        if (imageUri != null) {
            final ProgressDialog dialog = new ProgressDialog(this);
            dialog.setTitle("Отправка…");
            dialog.show();

            StorageReference ref = mStorageRef.child(STORAGE_PATH + System.currentTimeMillis() + "." + getImageExt(imageUri));

            ref.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    dialog.dismiss();

                    double latitude = Double.parseDouble(editLat1.getText().toString() + "." + editLat2.getText().toString());
                    double longitude = Double.parseDouble(editLong1.getText().toString() + "." + editLong2.getText().toString());

                    String workTime;
                    String workTimeBreak;
                    String id = null;

                    if (editWorkTime.getText().toString().isEmpty()) {
                        workTime = null;
                    } else {
                        workTime = editWorkTime.getText().toString();
                    }

                    if (editWorkTimeBreak.getText().toString().isEmpty()) {
                        workTimeBreak = null;
                    } else {
                        workTimeBreak = editWorkTimeBreak.getText().toString();
                    }

                    switch (groupChoice) {
                        case 0:
                            id = "bat";
                            finalId = "battery";
                            break;
                        case 1:
                            id = "ppr";
                            finalId = "paper";
                            break;
                        case 2:
                            id = "gls";
                            finalId = "glass";
                            break;
                        case 3:
                            id = "mtl";
                            finalId = "metal";
                            break;
                        case 4:
                            id = "blb";
                            finalId = "bulb";
                            break;
                        case 5:
                            id = "dng";
                            finalId = "danger";
                            break;
                        case 6:
                            id = "oth";
                            finalId = "other";
                            break;
                        case 7:
                            id = "evt";
                            finalId = "event";
                            break;
                    }

                    @SuppressWarnings("ConstantConditions")
                    final MarkerDataUpload markerDataUpload = new MarkerDataUpload(id, finalId, editTitle.getText().toString(), editAddress.getText().toString(),
                            taskSnapshot.getDownloadUrl().toString(), workTime, workTimeBreak, editDetails.getText().toString(), editContactsPhone.getText().toString(), editContactsEmail.
                            getText().toString(), editContactsUrl.getText().toString(), latitude, longitude);

                    ValueEventListener postListener = new ValueEventListener() {
                        @SuppressLint("SetTextI18n")
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if (dataSnapshot.child("1").getChildrenCount() == 0) {
                                uploadData("1", markerDataUpload);
                            } else if (dataSnapshot.child("2").getChildrenCount() == 0) {
                                uploadData("2", markerDataUpload);
                            } else if (dataSnapshot.child("3").getChildrenCount() == 0) {
                                uploadData("3", markerDataUpload);
                            } else if (dataSnapshot.child("4").getChildrenCount() == 0) {
                                uploadData("4", markerDataUpload);
                            } else if (dataSnapshot.child("5").getChildrenCount() == 0) {
                                uploadData("5", markerDataUpload);
                            } else if (dataSnapshot.child("6").getChildrenCount() == 0) {
                                uploadData("6", markerDataUpload);
                            } else if (dataSnapshot.child("7").getChildrenCount() == 0) {
                                uploadData("7", markerDataUpload);
                            } else if (dataSnapshot.child("8").getChildrenCount() == 0) {
                                uploadData("8", markerDataUpload);
                            } else if (dataSnapshot.child("9").getChildrenCount() == 0) {
                                uploadData("9", markerDataUpload);
                            } else if (dataSnapshot.child("10").getChildrenCount() == 0) {
                                uploadData("10", markerDataUpload);
                            } else {
                                AlertDialog.Builder databaseIsFull = new AlertDialog.Builder(SendMarkerInfoActivity.this);
                                databaseIsFull.setTitle("Нет свободных слотов для запроса");
                                databaseIsFull.setMessage("Попробуйте отправить информацию чуть позже.");
                                databaseIsFull.setNegativeButton("Закрыть", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        dialogInterface.dismiss();
                                        finish();
                                    }
                                });
                                databaseIsFull.show();
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    };
                    mDatabase.addListenerForSingleValueEvent(postListener);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    dialog.dismiss();
                    Toast.makeText(SendMarkerInfoActivity.this, "UPLOAD FAILED", Toast.LENGTH_LONG).show();
                }
            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                    double uploadProgress = (taskSnapshot.getBytesTransferred() * 100 / taskSnapshot.getTotalByteCount());
                    dialog.setMessage("Выполнено " + (int) uploadProgress + "%");
                }
            });
        } else {
            Toast.makeText(SendMarkerInfoActivity.this, "Пожалуйста, выберите изображение", Toast.LENGTH_LONG).show();
        }
    }

    private void uploadData(String position, MarkerDataUpload data) {
        mDatabase.child(position).setValue(data).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                AlertDialog.Builder uploadCompleteDialog = new AlertDialog.Builder(SendMarkerInfoActivity.this);
                uploadCompleteDialog.setTitle("Спасибо");
                uploadCompleteDialog.setMessage("Данные Вашего экопункта успешно отправлены в раздел модерации " +
                        "для дальнейшего рассмотрения.");
                uploadCompleteDialog.setPositiveButton("Закрыть", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });
                uploadCompleteDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialogInterface) {
                        DatabaseReference mTimesSentProgress = FirebaseDatabase.getInstance().getReference()
                                .child("users").child(user.getUid()).child("timesSent");
                        onTimesSentProfileCount(mTimesSentProgress);
                        finish();
                    }
                });
                uploadCompleteDialog.show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(SendMarkerInfoActivity.this, "Произошла ошибка отправки данных экопункта. Пожалуйста, " +
                        "проверьте подключение к Интернету.", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void showSummary() {
        String workTime;
        String workTimeBreak;
        if (!editWorkTime.getText().toString().isEmpty()) {
            workTime = editWorkTime.getText().toString();
        } else {
            workTime = "Круглосуточно";
        }
        if (!editWorkTimeBreak.getText().toString().isEmpty()) {
            workTimeBreak = editWorkTimeBreak.getText().toString();
        } else {
            workTimeBreak = "Без перерывов";
        }
        AlertDialog.Builder summaryDialog = new AlertDialog.Builder(this);
        summaryDialog.setTitle("Проверьте предоставленную информацию:");
        summaryDialog.setMessage("Название: " + editTitle.getText().toString() + "\n" +
                "Адрес: " + editAddress.getText().toString() + "\n" +
                "Описание: " + editDetails.getText().toString() + "\n" +
                "Время работы: " + workTime + "\n" +
                "Перерыв: " + workTimeBreak + "\n" +
                "Телефонный номер: " + editContactsPhone.getText().toString() + "\n" +
                "Адрес электронной почты: " + editContactsEmail.getText().toString() + "\n" +
                "Вебсайт: " + editContactsUrl.getText().toString() + "\n" +
                "Широта: " + editLat1.getText().toString() + "." + editLat2.getText().toString() + "\n" +
                "Долгота: " + editLong1.getText().toString() + "." + editLong2.getText().toString() + "\n");
        summaryDialog.setPositiveButton("Далее", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                AlertDialog.Builder finalDialog = new AlertDialog.Builder(SendMarkerInfoActivity.this);
                finalDialog.setTitle("Последний шаг");
                finalDialog.setMessage("Нажимая \"Отправить\", Вы подтверждаете, что предоставленная Вами информация не " +
                        "содержит неприемлимых материалов и получена из проверенных источников. Кроме того, " +
                        "учтите, что в консоли модерации есть всего 10 слотов для предложенных пунктов в " +
                        "целях упразднения спама. Вполне возможно, " +
                        "что все слоты заняты. В таком случае попробуйте отправить информацию чуть позже.");
                finalDialog.setPositiveButton("Отправить", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        upload();
                    }
                });
                finalDialog.setNegativeButton("Отмена", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                });
                finalDialog.show();
            }
        });
        summaryDialog.setNegativeButton("Изменить", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        });
        summaryDialog.show();
    }

    private void onTimesSentProfileCount(DatabaseReference postRef) {
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
}
