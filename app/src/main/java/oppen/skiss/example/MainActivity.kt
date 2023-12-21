package oppen.skiss.example

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import kotlinx.android.synthetic.main.activity_main.*
import oppen.skiss.example.demos.*
import oppen.skiss.lib.Skiss

class MainActivity : AppCompatActivity() {

  private var skiss: Skiss? = null

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
   // window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN)

    setContentView(R.layout.activity_main)

    ArrayAdapter.createFromResource(this, R.array.demo_array, android.R.layout.simple_spinner_item).also { adapter ->
      adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

      demo_selection_dropdown.adapter = adapter

      demo_selection_dropdown.setOnTouchListener { _, event ->
        skiss?.pause()
        super.onTouchEvent(event)
      }

      demo_selection_dropdown.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
        override fun onNothingSelected(parent: AdapterView<*>?) {
          skiss?.unpause()
        }

        override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
          skiss?.stop()
          when (position) {
            0 -> skiss = GravitySkiss(skiss_view){
              onTouch()
            }
            1 -> skiss = GrodyngelSkiss(skiss_view){
              onTouch()
            }
            2 -> skiss = MetaballSkiss(skiss_view){
              onTouch()
            }
            3 -> skiss = SimpleDemoSkiss(skiss_view){
              onTouch()
            }
            4 -> skiss = LissajousSkiss(skiss_view){
              onTouch()
            }
          }
          skiss?.start()
        }
      }
    }
  }

  private fun onTouch() {
    when (header.visibility) {
        View.VISIBLE -> {
          header.visibility = View.GONE
        }
        else -> {
          header.visibility = View.VISIBLE
        }
    }
  }
}
