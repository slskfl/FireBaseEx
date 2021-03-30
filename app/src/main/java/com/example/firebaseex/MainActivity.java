package com.example.firebaseex;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/*
 1. https://firebase.google.com/
 2. 홈페이지에서 프로젝트 만들기
 3. 다운로드 파일 넣기
 4. manigest, bupild 코드 추사
 */
public class MainActivity extends AppCompatActivity {
    EditText edtId, edtName, edtEmail, edtTel;
    Button btnSelect, btnInsert, btnUpdate, btnDelete;
    ListView dbListView;
    DatabaseReference reference;
    String userId, name, email, tel;
    ArrayAdapter<String> adapter;
    static ArrayList<String> arrayIndex = new ArrayList<String>();
    static ArrayList<String> arrayData = new ArrayList<String>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        edtId=findViewById(R.id.edtId);
        edtName=findViewById(R.id.edtName);
        edtEmail=findViewById(R.id.edtEmail);
        edtTel=findViewById(R.id.edtTel);
        btnSelect=findViewById(R.id.btnSelect);
        btnInsert=findViewById(R.id.btnInsert);
        btnUpdate=findViewById(R.id.btnUpdate);
        btnDelete=findViewById(R.id.btnDelete);
        dbListView =findViewById(R.id.DBlistView);

        adapter=new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, arrayData);
        dbListView.setAdapter(adapter);
        dbListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String tempData[]=arrayData.get(position).split(" ");
                edtId.setText(tempData[0]);
                edtName.setText(tempData[1]);
                edtEmail.setText(tempData[2]);
                edtTel.setText(tempData[3]);

            }
        });
        dbListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                String tempData[]=arrayData.get(position).split(" ");
                userId=tempData[0];
                AlertDialog.Builder builder=new AlertDialog.Builder(MainActivity.this);
                builder.setTitle("데이터 삭제");
                builder.setMessage(userId+" 님의 데이터를 삭제하시겠습니까?");
                builder.setPositiveButton("예", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        postFirebaseDateBase(false);
                        getFireDataBase();
                        showToast("데이터를 삭제했습니다.");
                    }
                });
                builder.setNegativeButton("아니오", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        showToast("삭제를 취소했습니다.");
                    }
                });
                builder.create();
                builder.show();
                return false;
            }
        });
        btnInsert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userId =edtId.getText().toString();
                name=edtName.getText().toString();
                email=edtEmail.getText().toString();
                tel=edtTel.getText().toString();
                if(!isExistID()){
                    postFirebaseDateBase(true);
                    getFireDataBase();
                    edtId.setText("");
                    edtName.setText("");
                    edtEmail.setText("");
                    edtTel.setText("");
                }else{
                    showToast("이미 존재하는 ID입니다.");
                    edtId.requestFocus();
                }
            }
        });
        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userId = edtId.getText().toString();
                name = edtName.getText().toString();
                email = edtEmail.getText().toString();
                tel = edtTel.getText().toString();
                postFirebaseDateBase(true);
                getFireDataBase();
                edtId.setText("");
                edtName.setText("");
                edtEmail.setText("");
                edtTel.setText("");
            }

        });
    }
    //아이디 중복 체크
    public boolean isExistID(){
        //중복되면 true, 중복되지 않았으면 false
        boolean isExist=arrayIndex.contains(userId);
        return isExist;
    }
    //데이터 저장, 수정 메서드
    public void postFirebaseDateBase(Boolean add){
        reference= FirebaseDatabase.getInstance().getReference(); //DB 참조
        Map<String, Object> childUpdates=new HashMap<>();
        Map<String, Object> postValues=new HashMap<>();
        //add가 false이기 때문에 저장(id가 중복이 아님)
        if(add){
            FirebasePost post=new FirebasePost(userId, name, email, tel);
            postValues=post.toMap();
        }
        // addrk true이기 때문에 수정
        childUpdates.put("/id_list/"+userId, postValues);
        reference.updateChildren(childUpdates);
    }
    //데이터 조회 메서드
    public void getFireDataBase(){
        ValueEventListener eventListener=new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                //앞에 있는 자료를 삭제하고 다시 처음부터 불러오기(계속 쌓이는 것을 방지)
                arrayData.clear();
                arrayIndex.clear();
                for(DataSnapshot dataSnapshot:snapshot.getChildren()){
                    //하위 항목을 가져옴(키와 값을 가져옴)
                    String key=dataSnapshot.getKey();
                    FirebasePost get=dataSnapshot.getValue(FirebasePost.class);
                    String info[]={get.id, get.name, get.email, get.tel}; //숫자일 경우 String.valueOf(값) 사용하기
                    String result=info[0]+" "+info[1]+" "+info[2]+" "+info[3];
                    arrayData.add(result);
                    arrayIndex.add(key);
                }
                adapter.clear();
                adapter.addAll(arrayData);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                showToast("데이터베이스 로드 실패");
            }
        };
    }
    void showToast(String msg){
        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
    }
}