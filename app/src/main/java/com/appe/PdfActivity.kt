package com.appe

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import com.github.barteksc.pdfviewer.PDFView
import java.io.File

class PdfActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pdf)

        val pdfView = findViewById<PDFView>(R.id.pdfView)
        val progressBar = findViewById<ProgressBar>(R.id.progressBar)
        val emptyText = findViewById<TextView>(R.id.tvEmpty)
        val namaFile = intent.getStringExtra("extraFile")

        lifecycleScope.launchWhenStarted {
            progressBar.isVisible = true
            try {
                if(namaFile.isNullOrBlank()){
                    emptyText.isVisible = true
                }else{
                    val pathFile = baseContext.filesDir.path+"/"+namaFile
                    val file = File(pathFile)
                    Log.e("PDFActivity", "Path Filenye ni wak: $pathFile")
                    pdfView.fromFile(file).load()
                }
            } catch (e: Exception) {
                Log.e("PDFActivity", "Ade Error pulak wak.. ni ha -> $e")
                progressBar.isVisible = false
                emptyText.isVisible = true
            }
            progressBar.isVisible = false
        }
        Toast.makeText(this, "File Berhail diBuka", Toast.LENGTH_SHORT).show()
    }
}