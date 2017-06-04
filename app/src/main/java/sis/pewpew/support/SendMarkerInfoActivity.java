package sis.pewpew.support;

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
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;

import sis.pewpew.R;

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
    public static final String STORAGE_PATH = "image/";
    public static final String DATABASE_PATH = "image";
    private Uri imageUri;
    public static final int REQUEST_CODE = 5;

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

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Выберите изображение"), REQUEST_CODE);
            }
        });

        Button chooseMarkerGroupButton = (Button) findViewById(R.id.choose_marker_group_button);

        chooseMarkerGroupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(SendMarkerInfoActivity.this);
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

                            }
                        });
                        finalDialog.show();
                    }
                });
                builder.show();
            }
        });
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
                    Toast.makeText(SendMarkerInfoActivity.this, "UPLOADED", Toast.LENGTH_LONG).show();
                    mDatabase.child("console").child("order1").child("iconUrl").setValue(taskSnapshot.getDownloadUrl().toString());
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
            Toast.makeText(SendMarkerInfoActivity.this, "PLEASE, SELECT IMAGE", Toast.LENGTH_LONG).show();
        }
    }
}
