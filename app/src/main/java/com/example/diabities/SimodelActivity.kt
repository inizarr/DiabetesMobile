package com.example.diabities

import android.annotation.SuppressLint
import android.content.res.AssetManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import org.tensorflow.lite.Interpreter
import java.io.FileInputStream
import java.nio.MappedByteBuffer
import java.nio.channels.FileChannel

class SimodelActivity : AppCompatActivity() {

    private lateinit var interpreter: Interpreter
    private val mModelPath = "diabetes.tflite"

    private lateinit var resultText: TextView
    private lateinit var Pregnancies: EditText
    private lateinit var Glucose: EditText
    private lateinit var BloodPressure: EditText
    private lateinit var SkinThickness: EditText
    private lateinit var BMI: EditText
    private lateinit var DiabetesPedigreeFunction: EditText
    private lateinit var Age: EditText
    private lateinit var checkButton : Button
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_simodel)

        val btnBack = findViewById<ImageButton>(R.id.btnBack)
        btnBack.setOnClickListener {
            finish()
        }

        resultText = findViewById(R.id.txtResult)
        Pregnancies = findViewById(R.id.Pregnancies)
        Glucose = findViewById(R.id.Glucose)
        BloodPressure = findViewById(R.id.BloodPressure)
        SkinThickness = findViewById(R.id.SkinThickness)
        BMI = findViewById(R.id.BMI)
        DiabetesPedigreeFunction = findViewById(R.id.DiabetesPedigreeFunction)
        Age = findViewById(R.id.Age)
        checkButton = findViewById(R.id.btnCheck)

        checkButton.setOnClickListener {
            var result = doInference(
                Pregnancies.text.toString(),
                Glucose.text.toString(),
                BloodPressure.text.toString(),
                SkinThickness.text.toString(),
                BMI.text.toString(),
                DiabetesPedigreeFunction.text.toString(),
                Age.text.toString())
            runOnUiThread {
                if (result == 0) {
                    resultText.text = "Terkena Diabetes"
                }else if (result == 1){
                    resultText.text = "Tidak Terkena Diabetes"
                }
            }
        }
        initInterpreter()
    }

    private fun initInterpreter() {
        val options = org.tensorflow.lite.Interpreter.Options()
        options.setNumThreads(7)
        options.setUseNNAPI(true)
        interpreter = org.tensorflow.lite.Interpreter(loadModelFile(assets, mModelPath), options)
    }

    private fun doInference(input1: String, input2: String, input3: String, input4: String, input5: String, input6: String, input7: String): Int{
        val inputVal = FloatArray(7)
        inputVal[0] = input1.toFloat()
        inputVal[1] = input2.toFloat()
        inputVal[2] = input3.toFloat()
        inputVal[3] = input4.toFloat()
        inputVal[4] = input5.toFloat()
        inputVal[5] = input6.toFloat()
        inputVal[6] = input7.toFloat()
        val output = Array(1) { FloatArray(2) }
        interpreter.run(inputVal, output)

        Log.e("result", (output[0].toList()+" ").toString())

        return output[0].indexOfFirst { it == output[0].maxOrNull() }
    }

    private fun loadModelFile(assetManager: AssetManager, modelPath: String): MappedByteBuffer{
        val fileDescriptor = assetManager.openFd(modelPath)
        val inputStream = FileInputStream(fileDescriptor.fileDescriptor)
        val fileChannel = inputStream.channel
        val startOffset = fileDescriptor.startOffset
        val declaredLength = fileDescriptor.declaredLength
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength)
    }
}