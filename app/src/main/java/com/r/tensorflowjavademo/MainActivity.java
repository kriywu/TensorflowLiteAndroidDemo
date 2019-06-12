package com.r.tensorflowjavademo;

import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.nfc.Tag;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.tensorflow.lite.Interpreter;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    public static final String TAG = "MainActivity";
    private TextView tv_output;
    private EditText et_input;
    private Button btn_compute;
    private Interpreter interpreter = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tv_output = findViewById(R.id.tv_output);
        et_input = findViewById(R.id.et_input);
        btn_compute = findViewById(R.id.btn_compute);
        btn_compute.setOnClickListener(this);

        // 小于1M的文件会读取失败，要把tffile后缀修改为MP3
        try {
            interpreter = new Interpreter(loadModel(getAssets(), "model.tflite"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // 加载模型文件
    MappedByteBuffer loadModel(AssetManager assest, String modelFile) throws IOException {
        AssetFileDescriptor fileDescriptor = assest.openFd(modelFile);
        FileInputStream inputStream = new FileInputStream(fileDescriptor.getFileDescriptor());
        FileChannel fileChannel = inputStream.getChannel();  // 使用文件通道
        long startOffset = fileDescriptor.getStartOffset();
        long declaredLength = fileDescriptor.getDeclaredLength();
        // 内存映射的方式读取文件，NIO技术可以更快读取更大的文件
        MappedByteBuffer buffer = fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength);
        interpreter = new Interpreter(buffer);
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength);
    }

    @Override
    public void onClick(View v) {
        int in = Integer.valueOf(et_input.getText().toString()); // 获取输入值
        Log.d(TAG, "onClick: " + in);
        int[] input = {in}; // 一个输入
        int[] output = {0}; // 一个输出
        interpreter.run(input, output); // 模型计算
        Log.d(TAG, "onClick: " + output[0]);
        tv_output.setText(String.valueOf(output[0]));
    }
}
