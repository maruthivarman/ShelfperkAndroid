package com.shelfperk.shelfperktextreader

import android.Manifest
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.text.TextUtils
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.Text
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import com.shelfperk.shelfperktextreader.model.AntonymsResponse
import com.shelfperk.shelfperktextreader.model.NewsResponse
import com.shelfperk.shelfperktextreader.model.SynonymsResponse
import com.shelfperk.shelfperktextreader.model.ThesaurusResponse
import com.shelfperk.shelfperktextreader.network.APIClient
import com.shelfperk.shelfperktextreader.network.APIService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException


class MainActivity : AppCompatActivity() {

    private lateinit var captureImage: Button
    private lateinit var galleryImage: Button
    private lateinit var processImage: Button
    private lateinit var clearAll: Button
    private lateinit var progress: ProgressBar
    private lateinit var image: ImageView
    private lateinit var imageHolder: CardView
    private lateinit var scannedText: TextView
    private lateinit var synonymsTextView: TextView
    private lateinit var antonymsTextView: TextView
    private lateinit var thesaurusTextView: TextView
    private lateinit var selectionButtons: LinearLayout
    private lateinit var synonymsLayout: LinearLayout
    private lateinit var antonymsLayout: LinearLayout
    private lateinit var thesaurusLayout: LinearLayout
    private lateinit var newsLayout: LinearLayout
    private lateinit var newsRecyclerView: RecyclerView
    private lateinit var newsAdapter: NewsAdapter
    private var bitmap: Bitmap? = null
    private var synonymsResponse: SynonymsResponse? = null
    private var antonymsResponse: AntonymsResponse? = null
    private var thesaurusResponse: ThesaurusResponse? = null
    private var newsResponse: NewsResponse? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        captureImage = findViewById(R.id.captureImage)
        galleryImage = findViewById(R.id.selectFromStorage)
        processImage = findViewById(R.id.processImage)
        progress = findViewById(R.id.progress)
        image = findViewById(R.id.image)
        imageHolder = findViewById(R.id.imageHolder)
        scannedText = findViewById(R.id.scannedText)
        selectionButtons = findViewById(R.id.selectionButtons)
        clearAll = findViewById(R.id.clearAll)
        synonymsTextView = findViewById(R.id.synonymsTextView)
        antonymsTextView = findViewById(R.id.antonymsTextView)
        thesaurusTextView = findViewById(R.id.thesaurusTextView)
        synonymsLayout = findViewById(R.id.synonymsLayout)
        antonymsLayout = findViewById(R.id.antonymsLayout)
        thesaurusLayout = findViewById(R.id.thesaurusLayout)
        newsLayout = findViewById(R.id.newsLayout)
        newsRecyclerView = findViewById(R.id.newsRecyclerView)

        scannedText.visibility = View.GONE
        image.visibility = View.GONE
        processImage.visibility = View.GONE
        clearAll.visibility = View.GONE
        synonymsLayout.visibility = View.GONE
        antonymsLayout.visibility = View.GONE
        thesaurusLayout.visibility = View.GONE
        newsLayout.visibility = View.GONE

        captureImage.setOnClickListener {
            captureAnImage()
        }
        galleryImage.setOnClickListener {
            selectFromStorage()
        }
        processImage.setOnClickListener {
            processImage()
        }
        clearAll.setOnClickListener {
            hideAll()
        }
    }

    private fun captureAnImage() {
        image.visibility = View.VISIBLE
        processImage.visibility = View.VISIBLE
        if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(arrayOf(Manifest.permission.CAMERA), CAMERA_REQUEST_CODE)
        } else {
            val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            if (cameraIntent.resolveActivity(packageManager) != null) {
                startActivityForResult(cameraIntent, CAPTURE_IMAGE_CODE)
            } else {
                Toast.makeText(this, "Camera app not found!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun selectFromStorage() {
        image.visibility = View.VISIBLE
        processImage.visibility = View.VISIBLE
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(
            Intent.createChooser(
                intent,
                "Select from:"
            ),
            PICK_IMAGE_CODE
        )
    }

    private fun processImage() {

        progress.visibility = View.VISIBLE
        processImage.visibility = View.GONE
        clearAll.visibility = View.VISIBLE

        if (bitmap != null) {
            val inputImage = InputImage.fromBitmap(bitmap!!, 0)
            val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)
            recognizer.process(inputImage)
                .addOnSuccessListener { result: Text ->
                    val text = result.text
                    if (!TextUtils.isEmpty(text)) {
                        scannedText.visibility = View.VISIBLE
                        scannedText.text = text
                        progress.visibility = View.GONE
                        callSynonmysAPI(text)
                        callAntonymsAPI(text)
                        callThesaurusAPI(text)
                        callNewsAPI(text)
                    } else {
                        showAlertDialog(
                            "Oops!",
                            "No Text Detected!",
                            false,
                            "Ok",
                            { dialog, _ ->
                                run {
                                    hideAll()
                                    dialog.dismiss()
                                }
                            },
                        )
                    }
                }
                .addOnFailureListener { e: Exception ->
                    hideAll()
                    Toast.makeText(this, "Error occurred:\n${e.message}", Toast.LENGTH_SHORT).show()
                }
        }
    }

    private fun callSynonmysAPI(word: String) {
        var apiService: APIService = APIClient.client.create(APIService::class.java)
        val call = apiService.fetchSynonymsResult(word)
        call.enqueue(object : Callback<SynonymsResponse> {

            override fun onResponse(call: Call<SynonymsResponse>, response: Response<SynonymsResponse>) {
                println(response.body())
                synonymsLayout.visibility = View.VISIBLE
                synonymsResponse = response.body()
                if(synonymsResponse == null || synonymsResponse!!.equals("")) {
                    synonymsLayout.visibility = View.GONE
                }else{
                    getSynonyms(synonymsResponse)
                 }
            }

            override fun onFailure(call: Call<SynonymsResponse>, t: Throwable) {
                showAlertDialog(
                    "Oops!",
                    "Please try again with clear text!",
                    false,
                    "Ok",
                    { dialog, _ ->
                        run {
                            hideAll()
                            dialog.dismiss()
                        }
                    },
                )
            }
        })

    }

    private fun getSynonyms(synonymsResponse: SynonymsResponse?) {
        val allSynonyms = StringBuilder()
        for (s in synonymsResponse?.synonyms!!) {
            if (allSynonyms.isNotEmpty()) {
                allSynonyms.append("\n")
            }
            allSynonyms.append(s)
        }
        synonymsTextView.text = allSynonyms.toString()
    }

    private fun callAntonymsAPI(word: String) {
        var apiService: APIService = APIClient.client.create(APIService::class.java)
        val call = apiService.fetchAntonymsResult(word)
        call.enqueue(object : Callback<AntonymsResponse> {

            override fun onResponse(call: Call<AntonymsResponse>, response: Response<AntonymsResponse>) {
                println(response.body())
                antonymsLayout.visibility = View.VISIBLE
                antonymsResponse = response.body()
                if(antonymsResponse == null || antonymsResponse!!.equals("")) {
                    antonymsLayout.visibility = View.GONE
                }else{
                    getAntonyms(antonymsResponse)
                }
            }

            override fun onFailure(call: Call<AntonymsResponse>, t: Throwable) {
                showAlertDialog(
                    "Oops!",
                    "Please try again with clear text!",
                    false,
                    "Ok",
                    { dialog, _ ->
                        run {
                            hideAll()
                            dialog.dismiss()
                        }
                    },
                )
            }
        })

    }

    private fun getAntonyms(antonymsResponse: AntonymsResponse?) {
        val allAntonyms = StringBuilder()
        for (s in antonymsResponse?.antonyms!!) {
            if (allAntonyms.isNotEmpty()) {
                allAntonyms.append("\n")
            }
            allAntonyms.append(s)
        }
        antonymsTextView.text = allAntonyms.toString()
    }

    private fun callThesaurusAPI(word: String) {
        var apiService: APIService = APIClient.client.create(APIService::class.java)
        val call = apiService.fetchThesaurusResult(word)
        call.enqueue(object : Callback<ThesaurusResponse> {

            override fun onResponse(call: Call<ThesaurusResponse>, response: Response<ThesaurusResponse>) {
                println(response.body())
                thesaurusLayout.visibility = View.VISIBLE
                thesaurusResponse = response.body()
                if(thesaurusResponse == null || thesaurusResponse!!.equals("")) {
                    thesaurusLayout.visibility = View.GONE
                }else{
                    getThesaurus(thesaurusResponse)
                }
            }

            override fun onFailure(call: Call<ThesaurusResponse>, t: Throwable) {
                showAlertDialog(
                    "Oops!",
                    "Please try again with clear text!",
                    false,
                    "Ok",
                    { dialog, _ ->
                        run {
                            hideAll()
                            dialog.dismiss()
                        }
                    },
                )
            }
        })

    }

    private fun getThesaurus(thesaurusResponse: ThesaurusResponse?) {
        val allThesaurus = StringBuilder()
        for (s in thesaurusResponse?.similarTo!!) {
            if (allThesaurus.isNotEmpty()) {
                allThesaurus.append("\n")
            }
            allThesaurus.append(s)
        }
        thesaurusTextView.text = allThesaurus.toString()
    }

    private fun callNewsAPI(word: String) {
        var apiService: APIService = APIClient.clientNews.create(APIService::class.java)
        val call = apiService.getNews(word)
        call.enqueue(object : Callback<NewsResponse> {

            override fun onResponse(call: Call<NewsResponse>, response: Response<NewsResponse>) {
                println(response.body())
                newsResponse = response.body()
                newsLayout.visibility = View.VISIBLE
                val layoutManager = LinearLayoutManager(applicationContext)
                newsRecyclerView.layoutManager = layoutManager
                newsRecyclerView.itemAnimator = DefaultItemAnimator()
                newsAdapter = NewsAdapter(newsResponse)
                newsRecyclerView.adapter = newsAdapter

            }

            override fun onFailure(call: Call<NewsResponse>, t: Throwable) {
                showAlertDialog(
                    "Oops!",
                    "Please try again with clear text!",
                    false,
                    "Ok",
                    { dialog, _ ->
                        run {
                            hideAll()
                            dialog.dismiss()
                        }
                    },
                )
            }
        })

    }

    public override fun onActivityResult(
        requestCode: Int,
        resultCode: Int,
        data: Intent?
    ) {
        super.onActivityResult(
            requestCode,
            resultCode,
            data
        )

        if (requestCode == PICK_IMAGE_CODE && resultCode == RESULT_OK && data != null && data.data != null) {
            try {
                bitmap = MediaStore.Images.Media
                    .getBitmap(
                        this.contentResolver,
                        data.data
                    )
                Glide.with(this)
                    .load(bitmap)
                    .into(image)
                showAll()
            } catch (e: IOException) {
                hideAll()
            }
        }

        if (requestCode == CAPTURE_IMAGE_CODE) {
            if (resultCode == RESULT_OK) {
                bitmap = data!!.extras!!["data"] as Bitmap?
                Glide.with(this)
                    .load(bitmap)
                    .into(image)
                showAll()
            } else {
                hideAll()
            }
        }
    }

    private fun hideAll() {
        image.setImageResource(0)
        imageHolder.visibility = View.GONE
        processImage.visibility = View.GONE
        selectionButtons.visibility = View.VISIBLE
        progress.visibility = View.GONE
        clearAll.visibility = View.GONE
        scannedText.visibility = View.GONE
        synonymsLayout.visibility = View.GONE
        antonymsLayout.visibility = View.GONE
        thesaurusLayout.visibility = View.GONE
        newsLayout.visibility = View.GONE
        bitmap = null
    }

    private fun showAll() {
        imageHolder.visibility = View.VISIBLE
        processImage.visibility = View.VISIBLE
        selectionButtons.visibility = View.GONE
    }

    fun showAlertDialog(
        title: String,
        message: String,
        isCancelable: Boolean,
        positiveBtnText: String? = "",
        positiveBtnOnClickListener: (DialogInterface, Int) -> Unit = { _, _ -> },
        negativeBtnText: String = "",
        negativeBtnOnClickListener: (DialogInterface, Int) -> Unit = { _, _ -> },
        neutralBtnText: String = "",
        neutralBtnOnClickListener: (DialogInterface, Int) -> Unit = { _, _ -> }
    ) {
        val builder = AlertDialog.Builder(this, R.style.AlertDialogTheme)
        builder.setTitle(title)
            .setMessage(message)
            .setCancelable(isCancelable)
            .setPositiveButton(positiveBtnText) { dialog, which ->
                positiveBtnOnClickListener(dialog, which)
            }
            .setNegativeButton(negativeBtnText) { dialog, which ->
                negativeBtnOnClickListener(dialog, which)
            }
            .setNeutralButton(neutralBtnText) { dialog, which ->
                neutralBtnOnClickListener(dialog, which)
            }
        val alert = builder.create()
        alert.window!!.setBackgroundDrawableResource(R.drawable.round_dialog)
        alert.show()
    }

    companion object {
        private const val PICK_IMAGE_CODE = 0
        private const val CAPTURE_IMAGE_CODE = 1
        private const val CAMERA_REQUEST_CODE = 2
    }
}