package com.june.healthmail.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.june.healthmail.R;
import com.june.healthmail.model.PostModel;
import com.june.healthmail.model.UserInfo;
import com.june.healthmail.untils.BitmapTools;
import com.june.healthmail.untils.PreferenceHelper;

import java.io.File;
import java.io.IOException;

import cn.bmob.v3.BmobObject;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;
import cn.bmob.v3.listener.UploadFileListener;

/**
 * Created by bjhujunjie on 2017/4/8.
 */

public class NewPostActivity extends Activity implements View.OnClickListener {

    private TextView tvSendPost;
    private ImageView ivAddPicture;
    private ImageView ivShowPicture;
    private EditText etPostContent;
    private ImageView ivBack;
    private String picturePath;
    private Uri photoUri;
    private File file;
    private Bitmap photo;
    private UserInfo userInfo;

    private ProgressDialog progressDialog;

    private static final int REQUEST_CODE_PICK_PIC = 10;
    private static final int REQUEST_CODE_TAKE_PHOTO = 11;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        userInfo = BmobUser.getCurrentUser(UserInfo.class);
        setContentView(R.layout.activity_new_post);
        initView();
        setOnListener();
        init();
    }


    private void initView() {
        tvSendPost = (TextView) findViewById(R.id.tv_send_post);
        etPostContent = (EditText) findViewById(R.id.post_content);
        ivBack = (ImageView) findViewById(R.id.img_back);
        ivAddPicture = (ImageView) findViewById(R.id.iv_add_picture);
        ivShowPicture = (ImageView) findViewById(R.id.iv_show_picture);
    }

    private void setOnListener() {
        tvSendPost.setOnClickListener(this);
        ivBack.setOnClickListener(this);
        ivAddPicture.setOnClickListener(this);
    }

    private void init() {
        //显示输入法
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                showKeyboard();
            }
        }, 200);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_send_post:
                String post_content = etPostContent.getText().toString().trim();
                PostModel postModel = new PostModel(userInfo.getObjectId());
                postModel.setOwnerName(userInfo.getUsername());
                postModel.setPostContent(post_content);
                sendNewPost(postModel);
                //GroupSqlManager.getInstance();
                break;
            case R.id.img_back:
                finish();
                break;
            case R.id.iv_add_picture:
                showChooseDialog();
                break;
            default:
                break;
        }
    }

    /**
     * 弹出拍照上传和相册选择对话框
     */
    private void showChooseDialog() {
        final CharSequence[] items = {"相册", "拍照"};
        AlertDialog dlg = new AlertDialog.Builder(this)
                .setTitle("选择图片")
                .setItems(items, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int item) {
                        // 这里item是根据选择的方式，
                        if (item == 0) {
                            //相册选择
                            Intent intent = new Intent(Intent.ACTION_PICK);
                            intent.setType("image/*");
                            startActivityForResult(intent, REQUEST_CODE_PICK_PIC);
                        } else {
                            //拍照上传
                            destoryBimap();
                            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                            Log.e("NewPostActivity", "sd 卡路径：" +  Environment.getExternalStorageDirectory());
                            if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                                String saveDir = Environment.getExternalStorageDirectory() + "/temp";
                                File dir = new File(saveDir);
                                if (!dir.exists()) {
                                    dir.mkdir();
                                }
                                file = new File(saveDir, "/takephoto.png");
                                Log.e("test","file path = " + file.getPath());
                                file.delete();
                                if (!file.exists()) {
                                    try {
                                        file.createNewFile();
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                        return;
                                    }
                                }
                                intent.putExtra(MediaStore.EXTRA_OUTPUT,  Uri.fromFile(file));
                                startActivityForResult(intent, REQUEST_CODE_TAKE_PHOTO);

                            } else {
//                                Toast.makeText(MainActivity.this, 未找到存储卡，无法存储照片！,
//                                        Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                }).create();
        dlg.show();
    }

    /**
     * 销毁图片文件
     */
    private void destoryBimap() {
        if (photo != null && !photo.isRecycled()) {
            photo.recycle();
            photo = null;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_CODE_PICK_PIC) {
                if (data == null) {
                    Log.e("NewPostActivity","pick pic failed");
                    return;
                } else {
                    Uri uri = data.getData();
                    Bitmap bitmap = getOriginalBitmap(uri);

                    BitmapTools.compressBmpToFile(bitmap);

//          String[] proj = {MediaStore.Images.Media.DATA};
//          Cursor cursor = getContentResolver().query(uri, proj, null, null, null);
//          cursor.moveToFirst();
//          int columnIndex = cursor.getColumnIndex(proj[0]);
//          picturePath = cursor.getString(columnIndex);

                    //原来的获取图片路径的方式不用，改用压缩后的图片来上传
                    picturePath = Environment.getExternalStorageDirectory() + "/temp/temp.png";
                    ivShowPicture.setImageBitmap(bitmap);
                    ivShowPicture.setVisibility(View.VISIBLE);
                }
            }else if(requestCode == REQUEST_CODE_TAKE_PHOTO){
                if (file != null && file.exists()) {
                    BitmapFactory.Options options = new BitmapFactory.Options();
                    options.inSampleSize = 2;
                    photo = BitmapFactory.decodeFile(file.getPath(), options);
                    BitmapTools.compressBmpToFile(photo);

                    picturePath = Environment.getExternalStorageDirectory() + "/temp/temp.png";
                    ivShowPicture.setImageBitmap(photo);
                    ivShowPicture.setVisibility(View.VISIBLE);

                }
            }
        }
    }

    private Bitmap getOriginalBitmap(Uri photoUri) {
        if (photoUri == null) {
            return null;
        }
        Bitmap bitmap = null;
        try {
            ContentResolver conReslv = getContentResolver();
            // 得到选择图片的Bitmap对象
            bitmap = MediaStore.Images.Media.getBitmap(conReslv, photoUri);
        } catch (Exception e) {
            Log.e("NewPostActivity", "Media.getBitmap failed", e);
        }
        return bitmap;
    }



    private void sendNewPost(final PostModel postModel) {
        if (postModel.getPostContent().length() == 0) {
            Toast.makeText(NewPostActivity.this, "您不能发布空内容", Toast.LENGTH_SHORT).show();
            return;
        }
        String dialogInfo;
        if(picturePath != null) {
            dialogInfo = "发布带图片的帖子将消耗" + PreferenceHelper.getInstance().getCoinsCostForSpecialFunction()
                    + "个金币，是否继续发布？";
        }else {
            dialogInfo = "发布纯文字帖子将消耗" + PreferenceHelper.getInstance().getCoinsCostForPost()
                    + "个金币，是否继续发布？";
        }
        AlertDialog dialog = new AlertDialog.Builder(NewPostActivity.this)
                .setTitle("提示")
                .setMessage(dialogInfo)
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .setPositiveButton("发布", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        Log.d("test", "用户选择确定发布");

                        progressDialog = new ProgressDialog(NewPostActivity.this);
                        progressDialog.setMessage("正在发布中，请稍候...");
                        progressDialog.setCancelable(false);
                        progressDialog.show();

                        if(picturePath != null) {
                            if(userInfo.getCoinsNumber() < PreferenceHelper.getInstance().getCoinsCostForSpecialFunction()){
                                Toast.makeText(NewPostActivity.this, "金币扣余额不足", Toast.LENGTH_SHORT).show();
                                return;
                            }
                            userInfo.setCoinsNumber(userInfo.getCoinsNumber() -
                                    PreferenceHelper.getInstance().getCoinsCostForSpecialFunction());
                        }else {
                            if(userInfo.getCoinsNumber() < PreferenceHelper.getInstance().getCoinsCostForPost()){
                                Toast.makeText(NewPostActivity.this, "金币扣余额不足", Toast.LENGTH_SHORT).show();
                                return;
                            }
                            userInfo.setCoinsNumber(userInfo.getCoinsNumber() -
                                    PreferenceHelper.getInstance().getCoinsCostForPost());
                        }
                        userInfo.update(BmobUser.getCurrentUser().getObjectId(), new UpdateListener() {
                            @Override
                            public void done(BmobException e) {
                                if(e == null){
                                    Toast.makeText(NewPostActivity.this, "金币扣除成功", Toast.LENGTH_SHORT).show();
                                    createPostGroup(postModel);
                                }
                            }
                        });
                    }
                }).create();
        dialog.show();
    }

    private void insertObject(final BmobObject obj) {
        obj.save(new SaveListener<String>() {
            @Override
            public void done(String s, BmobException e) {
                if(progressDialog != null && progressDialog.isShowing()){
                    progressDialog.dismiss();
                }
                if (e == null) {
                    Toast.makeText(NewPostActivity.this, "发布成功", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(NewPostActivity.this, "发布失败," + e.toString(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void createPostGroup(final PostModel postModel) {
        if (picturePath != null) {
            //包含图片文件
            final BmobFile bmobFile = new BmobFile(new File(picturePath));
            postModel.setPostPicture(bmobFile);
            bmobFile.uploadblock(new UploadFileListener() {
                @Override
                public void done(BmobException e) {
                    if (e == null) {
                        insertObject(postModel);
                    } else {
                        if(progressDialog != null && progressDialog.isShowing()){
                            progressDialog.dismiss();
                        }
                        Toast.makeText(NewPostActivity.this, "文件上传失败,请检查您的网络环境", Toast.LENGTH_SHORT).show();
                        Log.e("NewPostActivity", "file upload,fail   error:" + e.toString());
                    }
                }
                @Override
                public void onProgress(Integer value) {
                    super.onProgress(value);
                }
            });
        } else {
            insertObject(postModel);
        }
        return ;
    }



    public void showKeyboard() {
        if (etPostContent != null) {
            etPostContent.setFocusable(true);
            etPostContent.setFocusableInTouchMode(true);
            etPostContent.requestFocus();
            InputMethodManager inputManager = (InputMethodManager) etPostContent.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            inputManager.showSoftInput(etPostContent, 0);
        }
    }
}
