package com.preritrajput.firestoreapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.Manifest;
import android.app.DownloadManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.StrictMode;
import android.text.format.DateFormat;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.preritrajput.firestoreapp.databinding.ActivityAddExperimentBinding;
import com.preritrajput.firestoreapp.databinding.ActivityExperimentsBinding;

import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.util.Units;
import org.apache.poi.xwpf.usermodel.Borders;
import org.apache.poi.xwpf.usermodel.BreakType;
import org.apache.poi.xwpf.usermodel.ParagraphAlignment;
import org.apache.poi.xwpf.usermodel.TableRowAlign;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.apache.poi.xwpf.usermodel.XWPFTableCell;
import org.apache.poi.xwpf.usermodel.XWPFTableRow;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTBody;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTBorder;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTDocument1;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTHMerge;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTPPr;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTSectPr;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTSpacing;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTVMerge;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STBorder;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STLineSpacingRule;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STMerge;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigInteger;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class ExperimentsActivity extends AppCompatActivity {

    private ActivityExperimentsBinding binding;
    String sem,cc,fn,subjn,uid,email,timeStamp,name,phone,enroll,uname;
    private Animation animShow, animHide;

    FirebaseAuth firebaseAuth;
    FirebaseUser user;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;

    ArrayList<ModelExperiments> subjectsList;
    AdapterExperiments adapterSubjects;

    private File filePath=null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        getWindow().setStatusBarColor(ContextCompat.getColor(this,R.color.black_trans80));
        binding = ActivityExperimentsBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE},
                PackageManager.PERMISSION_GRANTED);


        firebaseAuth=FirebaseAuth.getInstance();
        user=firebaseAuth.getCurrentUser();
        firebaseDatabase=FirebaseDatabase.getInstance();
        databaseReference=firebaseDatabase.getReference("Users");

        animShow = AnimationUtils.loadAnimation( this, R.anim.view_show);
        animHide = AnimationUtils.loadAnimation( this, R.anim.view_hide);

        Query query = databaseReference.orderByChild("email").equalTo(user.getEmail());
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds : snapshot.getChildren())
                {
                    enroll=""+ds.child("phone").getValue();
                    uname=""+ds.child("name").getValue();
                }

                filePath = new File(getExternalFilesDir(null), uname+"_"+enroll+".docx");

                try {
                    if (!filePath.exists()){
                        filePath.createNewFile();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        sem=getIntent().getStringExtra("Semester");
        cc=getIntent().getStringExtra("Code");
        fn=getIntent().getStringExtra("Faculty");
        subjn=getIntent().getStringExtra("Subject");
        uid=getIntent().getStringExtra("uid");
        email=getIntent().getStringExtra("email");
        timeStamp=getIntent().getStringExtra("timeStamp");
        name=getIntent().getStringExtra("name");
        phone=getIntent().getStringExtra("phone");

        binding.semAndSubj.setText(subjn+", Semester : "+sem);
        binding.courseCode.setText("Course Code : "+cc);
        binding.facultyName.setText("Faculty Name : "+fn);

        subjectsList= new ArrayList<>();

        LinearLayoutManager layoutManager1=new LinearLayoutManager(view.getContext(),LinearLayoutManager.VERTICAL,false);
        binding.experimentRecycler.setLayoutManager(layoutManager1);

        binding.shimmerLayout4.startShimmer();
        loadExperiments(timeStamp);


        binding.relativeLayout2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(subjectsList.size()==0)
                {
                    binding.relativeLayout2.setClickable(false);
                    binding.error.setVisibility(View.VISIBLE);
                    binding.error.startAnimation(animShow);
                    new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            binding.error.startAnimation(animHide);
                            binding.error.setVisibility(View.GONE);
                            binding.relativeLayout2.setClickable(true);
                        }
                    },3000);
                }
                else if(enroll.equals(""))
                {
                    startActivity(new Intent(ExperimentsActivity.this,EditProfileActivity.class));
                    Toast.makeText(ExperimentsActivity.this, "Please Add your Enrollment No. to Download the File.", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    binding.relativeLayout2.setBackgroundResource(R.color.grey);
                    binding.progressBar.setVisibility(View.VISIBLE);
                    binding.text.setVisibility(View.INVISIBLE);
                    binding.relativeLayout2.setClickable(false);

                    new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            generate();
                        }
                    },500);
                }
            }
        });

    }


    private void generate() {
        try {
            XWPFDocument xwpfDocument = new XWPFDocument();
            XWPFParagraph xwpfParagraph = xwpfDocument.createParagraph();
            xwpfParagraph.setAlignment(ParagraphAlignment.CENTER);
            XWPFRun xwpfRun = xwpfParagraph.createRun();

            xwpfRun.setText(subjn+" ("+cc+")");
            xwpfRun.setFontSize(16);
            xwpfRun.setFontFamily("Times New Roman");
            xwpfRun.addBreak();
            xwpfRun.setText("LAB FILE");
            xwpfRun.addBreak();
            xwpfRun.setText("BACHELOR OF TECHNOLOGY");
            xwpfRun.addBreak();
            xwpfRun.setText("(Computer Science and Engineering)");
            xwpfRun.addBreak();
            xwpfRun.setText("SEMESTER-"+sem);

            Drawable drawable = getResources().getDrawable(R.drawable.amity);
            BitmapDrawable bitmapDrawable = ((BitmapDrawable) drawable);
            Bitmap bitmap = bitmapDrawable.getBitmap();
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream); //use the compression format of your need
            InputStream is = new ByteArrayInputStream(stream.toByteArray());

            String imgFile = "amity.jpg";

            xwpfRun.addBreak();
            xwpfRun.setText("");
            xwpfRun.addBreak();
            xwpfRun.addPicture(is, XWPFDocument.PICTURE_TYPE_JPEG, imgFile, Units.toEMU(103), Units.toEMU(131)); // 200x200 pixels
            is.close();
            xwpfRun.addBreak();
            xwpfRun.setText("");

            XWPFParagraph xwpfParagraph1 = xwpfDocument.createParagraph();
            xwpfParagraph1.setAlignment(ParagraphAlignment.CENTER);
            XWPFRun xwpfRun1 = xwpfParagraph1.createRun();

            xwpfRun1.setText("Department of Computer Science & Engineering");
            xwpfRun1.setFontSize(18);
            xwpfRun1.setFontFamily("Times New Roman");

            XWPFParagraph xwpfParagraph2 = xwpfDocument.createParagraph();
            xwpfParagraph2.setAlignment(ParagraphAlignment.CENTER);
            XWPFRun xwpfRun2 = xwpfParagraph2.createRun();

            xwpfRun2.setText("AMITY SCHOOL OF ENGINEERING AND TECHNOLOGY AMITY UNIVERSITY UTTAR PRADESH");
            xwpfRun2.setFontSize(11);
            xwpfRun2.setFontFamily("Times New Roman");
            xwpfRun2.addBreak();
            xwpfRun2.setText("NOIDA, (U.P.), INDIA");
            xwpfRun2.addBreak();
            xwpfRun2.setText("");

            XWPFParagraph xwpfParagraph3 = xwpfDocument.createParagraph();
            xwpfParagraph3.setAlignment(ParagraphAlignment.CENTER);
            XWPFRun xwpfRun3 = xwpfParagraph3.createRun();

            xwpfRun3.setText("SUBMITTED BY-");
            xwpfRun3.setFontSize(16);
            xwpfRun3.setFontFamily("Times New Roman");
            xwpfRun3.addBreak();
            xwpfRun3.setText(uname);
            xwpfRun3.addBreak();
            xwpfRun3.setText(enroll);
            xwpfRun3.addBreak();
            xwpfRun3.setText("");

            XWPFParagraph xwpfParagraph4 = xwpfDocument.createParagraph();
            xwpfParagraph4.setAlignment(ParagraphAlignment.CENTER);
            XWPFRun xwpfRun4 = xwpfParagraph4.createRun();

            xwpfRun4.setText("SUBMITTED TO-");
            xwpfRun4.setFontSize(16);
            xwpfRun4.setFontFamily("Times New Roman");
            xwpfRun4.addBreak();
            xwpfRun4.setText(fn);

            XWPFParagraph xwpfParagraph5 = xwpfDocument.createParagraph();
            xwpfParagraph5.setPageBreak(true);
            xwpfParagraph5.setAlignment(ParagraphAlignment.CENTER);
            XWPFRun xwpfRun5 = xwpfParagraph5.createRun();

            xwpfRun5.setText("CONTENT");
            xwpfRun5.setFontSize(16);
            xwpfRun5.setFontFamily("Times New Roman");

            XWPFTable tab = xwpfDocument.createTable();
            XWPFTableRow row = tab.getRow(0); // First row

            XWPFParagraph paragraph6 = row.getCell(0).addParagraph();
            XWPFRun run6 = paragraph6.createRun();
            run6.setBold(true);
            run6.setText("Experiment No.");
            XWPFParagraph paragraph7 = row.addNewTableCell().addParagraph();
            XWPFRun run7 = paragraph7.createRun();
            run7.setBold(true);
            run7.setText("Name of Programs");
            XWPFParagraph paragraph8 = row.addNewTableCell().addParagraph();
            XWPFRun run8 = paragraph8.createRun();
            run8.setBold(true);
            run8.setText("Date of allotment of exp.");
            XWPFParagraph paragraph9 = row.addNewTableCell().addParagraph();
            XWPFRun run9= paragraph9.createRun();
            run9.setBold(true);
            run9.setText("Date of evaluation");
            XWPFParagraph paragraph10 = row.addNewTableCell().addParagraph();
            XWPFRun run10= paragraph10.createRun();
            run10.setBold(true);
            run10.setText("Max. Marks");
            XWPFParagraph paragraph11 = row.addNewTableCell().addParagraph();
            XWPFRun run11= paragraph11.createRun();
            run11.setBold(true);
            run11.setText("Marks obtained");
            XWPFParagraph paragraph12 = row.addNewTableCell().addParagraph();
            XWPFRun run12= paragraph12.createRun();
            run12.setBold(true);
            run12.setText("Signature of Faculty");

            for(int i=0;i<subjectsList.size();i++)
            {
                row = tab.createRow(); // Second Row
                Calendar calendar = Calendar.getInstance(Locale.getDefault());
                calendar.setTimeInMillis(Long.parseLong(subjectsList.get(i).timeStamp));
                final String pTime= DateFormat.format("dd/MM/yyyy", calendar).toString();
                row.getCell(0).setText(subjectsList.get(i).expNo);
                row.getCell(1).setText(subjectsList.get(i).aim);
                row.getCell(2).setText(pTime);
                row.getCell(3).setText("");
                row.getCell(4).setText("1");
                row.getCell(5).setText("");
                row.getCell(6).setText("");
            }

            for (int j=0;j<subjectsList.size();j++)
            {
                XWPFParagraph xwpfParagraph6 = xwpfDocument.createParagraph();
                xwpfParagraph6.setPageBreak(true);
                xwpfParagraph6.setAlignment(ParagraphAlignment.CENTER);
                XWPFRun xwpfRun6 = xwpfParagraph6.createRun();

                xwpfRun6.setText("Experiment - "+subjectsList.get(j).expNo);
                xwpfRun6.setFontSize(16);
                xwpfRun6.setBold(true);
                xwpfRun6.setFontFamily("Times New Roman");
                xwpfRun6.addBreak();
                xwpfRun6.setText("");

                XWPFParagraph xwpfParagraph7 = xwpfDocument.createParagraph();
                xwpfParagraph7.setAlignment(ParagraphAlignment.LEFT);
                XWPFRun xwpfRun7 = xwpfParagraph7.createRun();

                xwpfRun7.setText("Aim : "+subjectsList.get(j).aim);
                xwpfRun7.setFontSize(14);
                xwpfRun7.setFontFamily("Times New Roman");
                xwpfRun7.addBreak();
                xwpfRun7.setText("");
                xwpfRun7.addBreak();
                xwpfRun7.setText("Theory : "+subjectsList.get(j).theory);
                xwpfRun7.addBreak();
                xwpfRun7.setText("");
                xwpfRun7.addBreak();
                xwpfRun7.setText("Code : "+subjectsList.get(j).outputCode);
                xwpfRun7.addBreak();
                xwpfRun7.setText("");
                xwpfRun7.addBreak();
                xwpfRun7.setText("Output : ");
                xwpfRun7.addBreak();
                xwpfRun7.setText("");
                xwpfRun7.addBreak();

                String drawableRes=subjectsList.get(j).outputPic;
                StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
                StrictMode.setThreadPolicy(policy);
                try {
                    URL url = new URL(drawableRes);
                    Bitmap bitmapDrawable1 =BitmapFactory.decodeStream((InputStream)url.getContent());
                    ByteArrayOutputStream stream1 = new ByteArrayOutputStream();
                    bitmapDrawable1.compress(Bitmap.CompressFormat.JPEG, 100, stream1); //use the compression format of your need
                    InputStream is1 = new ByteArrayInputStream(stream1.toByteArray());

                    String imgFile1 = "output.jpg";

                    xwpfRun7.addPicture(is1, XWPFDocument.PICTURE_TYPE_JPEG, imgFile1, Units.toEMU(400), Units.toEMU(250)); // 200x200 pixels
                    is1.close();
                } catch (IOException e) {
                    //Log.e(TAG, e.getMessage());
                }
                xwpfRun7.addBreak(BreakType.PAGE);

                XWPFTable tab1 = xwpfDocument.createTable();
                XWPFTableRow row1 = tab1.getRow(0);
                tab1.setWidth("100%");

                XWPFParagraph paragraph = row1.getCell(0).addParagraph();
                paragraph.setAlignment(ParagraphAlignment.CENTER);
                XWPFRun run = paragraph.createRun();
                run.setBold(true);
                run.setText("Internal Assessment (Mandatory Experiment) Sheet for Lab Experiment");
                run.addBreak();
                run.setText("Department of Computer Science & Engineering");
                run.addBreak();
                run.setText("Amity University, Noida (UP)");

                //row1.getCell(0).setText("Internal Assessment (Mandatory Experiment) Sheet for Lab Experiment");
                row1.addNewTableCell().setText("");
                row1.addNewTableCell().setText("");
                row1.addNewTableCell().setText("");

                row1 = tab1.createRow(); // Second Row
                row1.getCell(0).setText("Programme");
                row1.getCell(1).setText("B.Tech-CSE");
                row1.getCell(2).setText("Course Name");
                row1.getCell(3).setText(subjn+" Lab");

                row1 = tab1.createRow(); // Second Row
                row1.getCell(0).setText("Course Code");
                row1.getCell(1).setText(cc);
                row1.getCell(2).setText("Semester");
                row1.getCell(3).setText(sem);

                row1 = tab1.createRow(); // Second Row
                row1.getCell(0).setText("Student Name");
                row1.getCell(1).setText(uname);
                row1.getCell(2).setText("Enrolment No.");
                row1.getCell(3).setText(enroll);

                row1 = tab1.createRow(); // Second Row
                XWPFParagraph paragraph1 = row1.getCell(0).addParagraph();
                paragraph1.setAlignment(ParagraphAlignment.CENTER);
                XWPFRun run1 = paragraph1.createRun();
                run1.setBold(true);
                run1.setText("Marking Criteria");
                row1.getCell(1).setText("");
                row1.getCell(2).setText("");
                row1.getCell(3).setText("");


                row1 = tab1.createRow(); // Second Row
                XWPFParagraph paragraph2 = row1.getCell(0).addParagraph();
                paragraph2.setAlignment(ParagraphAlignment.CENTER);
                XWPFRun run2 = paragraph2.createRun();
                run2.setBold(true);
                run2.setText("Criteria");
                XWPFParagraph paragraph3 = row1.getCell(1).addParagraph();
                paragraph3.setAlignment(ParagraphAlignment.CENTER);
                XWPFRun run3 = paragraph3.createRun();
                run3.setBold(true);
                run3.setText("Total Marks");
                XWPFParagraph paragraph4 = row1.getCell(2).addParagraph();
                paragraph4.setAlignment(ParagraphAlignment.CENTER);
                XWPFRun run4 = paragraph4.createRun();
                run4.setBold(true);
                run4.setText("Marks Obtained");
                XWPFParagraph paragraph5 = row1.getCell(3).addParagraph();
                paragraph5.setAlignment(ParagraphAlignment.CENTER);
                XWPFRun run5 = paragraph5.createRun();
                run5.setBold(true);
                run5.setText("Comments");

                row1 = tab1.createRow(); // Second Row
                row1.getCell(0).setText("Concept (A)");
                row1.getCell(1).setText("2");
                row1.getCell(2).setText("");
                row1.getCell(3).setText("");

                row1 = tab1.createRow(); // Second Row
                row1.getCell(0).setText("Implementation (B)");
                row1.getCell(1).setText("2");
                row1.getCell(2).setText("");
                row1.getCell(3).setText("");

                row1 = tab1.createRow(); // Second Row
                row1.getCell(0).setText("Performance (C)");
                row1.getCell(1).setText("2");
                row1.getCell(2).setText("");
                row1.getCell(3).setText("");

                row1 = tab1.createRow(); // Second Row
                row1.getCell(0).setText("Total ");
                row1.getCell(1).setText("6");
                row1.getCell(2).setText("");
                row1.getCell(3).setText("");

                XWPFTableCell cellRow1 = tab1.getRow(0).getCell(0);
                XWPFTableCell cellRow2 = tab1.getRow(4).getCell(0);

                cellRow1.getCTTc().addNewTcPr();
                cellRow1.getCTTc().getTcPr().addNewGridSpan();
                cellRow1.getCTTc().getTcPr().getGridSpan().setVal(BigInteger.valueOf(4L));

                cellRow2.getCTTc().addNewTcPr();
                cellRow2.getCTTc().getTcPr().addNewGridSpan();
                cellRow2.getCTTc().getTcPr().getGridSpan().setVal(BigInteger.valueOf(4L));

                cellRow1 = tab1.getRow(0).getCell(3);
                cellRow1.getCTTc().newCursor().removeXml();

                cellRow1 = tab1.getRow(0).getCell(2);
                cellRow1.getCTTc().newCursor().removeXml();

                cellRow1 = tab1.getRow(0).getCell(1);
                cellRow1.getCTTc().newCursor().removeXml();

                cellRow2 = tab1.getRow(4).getCell(3);
                cellRow2.getCTTc().newCursor().removeXml();

                cellRow2 = tab1.getRow(4).getCell(2);
                cellRow2.getCTTc().newCursor().removeXml();

                cellRow2 = tab1.getRow(4).getCell(1);
                cellRow2.getCTTc().newCursor().removeXml();

            }


            FileOutputStream fileOutputStream = new FileOutputStream(filePath);
            xwpfDocument.write(fileOutputStream);

            if (fileOutputStream!=null){
                fileOutputStream.flush();
                fileOutputStream.close();
            }
            xwpfDocument.close();

            Intent intent=new Intent(ExperimentsActivity.this,SuccessActivity.class);
            intent.putExtra("filename",uname+"_"+enroll+".docx");
            intent.putExtra("subject",subjn);
            intent.putExtra("course",cc);
            startActivity(intent);
        }
        catch (Exception e){
            Toast.makeText(this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }

        binding.relativeLayout2.setBackgroundResource(R.color.discordblue);
        binding.progressBar.setVisibility(View.INVISIBLE);
        binding.text.setVisibility(View.VISIBLE);
        binding.relativeLayout2.setClickable(true);


    }

    private void loadExperiments(String timeStamp) {
        DatabaseReference reference=FirebaseDatabase.getInstance().getReference("Experiments");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                subjectsList.clear();
                for(DataSnapshot ds:snapshot.getChildren())
                {
                    binding.experimentRecycler.setVisibility(View.VISIBLE);
                    ModelExperiments modelRecommended=ds.getValue(ModelExperiments.class);
                    if(modelRecommended.getSubTime().contains(timeStamp))
                    {
                        subjectsList.add(modelRecommended);
                    }

                }
                adapterSubjects=new AdapterExperiments(subjectsList,ExperimentsActivity.this);
                if(adapterSubjects.getItemCount()==0)
                {
                    binding.empty.setVisibility(View.VISIBLE);
                    binding.experimentRecycler.setVisibility(View.GONE);
                }
                else
                {

                    binding.empty.setVisibility(View.GONE);
                    binding.experimentRecycler.setVisibility(View.VISIBLE);

                }

                binding.experimentRecycler.setAdapter(adapterSubjects);
                binding.shimmerLayout4.stopShimmer();
                binding.shimmerLayout4.setVisibility(View.GONE);
                adapterSubjects.notifyDataSetChanged();
                binding.relativeLayout2.setClickable(true);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void back(View view) {
        ExperimentsActivity.super.onBackPressed();
    }

    public void addExp(View view) {
        Intent intent=new Intent(ExperimentsActivity.this,AddExperimentActivity.class);
        intent.putExtra("Semester",sem);
        intent.putExtra("Subject",subjn);
        intent.putExtra("Code",cc);
        intent.putExtra("Faculty",fn);
        intent.putExtra("uid",uid);
        intent.putExtra("email",email);
        intent.putExtra("timeStamp",timeStamp);
        startActivity(intent);
    }

    /*public void download(View view) {
        binding.error.setVisibility(View.VISIBLE);
        binding.error.startAnimation(animShow);
        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                binding.error.startAnimation(animHide);
                binding.error.setVisibility(View.GONE);
            }
        },3000);
    }*/
}