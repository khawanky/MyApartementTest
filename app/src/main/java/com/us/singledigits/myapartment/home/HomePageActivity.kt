package com.us.singledigits.myapartment.home

import android.graphics.BlurMaskFilter
import android.graphics.Color
import android.graphics.Paint
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.us.singledigits.myapartment.R
import jp.wasabeef.blurry.Blurry
import kotlinx.android.synthetic.main.activity_home_page.*


class HomePageActivity : AppCompatActivity() {

    var sections = ArrayList <Section>()
    var sectionAdapter:SectionAdapter?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home_page)
        setSupportActionBar(findViewById(R.id.toolbar))
        supportActionBar!!.setDisplayShowTitleEnabled(false)


        // TODO: Trying to Blur the background (I will blur it externally)
//        var paint = Paint()
//        paint.maskFilter = BlurMaskFilter(0.5f, BlurMaskFilter.Blur.NORMAL)
//        ivBackground.setLayerPaint(paint)


//        Blurry.with(this).radius(25).sampling(2).onto(linearViewBack.background)
//        Blurry.with(this).radius(10)
//            .sampling(8).color(Color.argb(66, 255, 255, 0))
//            .async().animate(500).onto(ivBackground)

//        val backgroundBitMap = (ivBackground.drawable as BitmapDrawable).bitmap
//        Blurry.with(this)
//            .radius(50)
//            .from(backgroundBitMap)
//            .into(ivBackground)

//        Blurry.with(this)
//            .radius(50)
//            .onto(linearViewBack)

        sections.add(Section("Doors", "ALL LOCKED", R.drawable.locked))
        sections.add(Section("Lights", "1 ON", R.drawable.closed_lamp))
        sections.add(Section("Thermostat", "HEATING", R.drawable.closed_lamp))
        sections.add(Section("Devices", "3 Total", R.drawable.tv_guide))
        sections.add(Section("TV guide", "", R.drawable.tv_guide))
        sections.add(Section("Devices", "3 Total", R.drawable.tv_guide))
        sections.add(Section("Devices", "3 Total", R.drawable.tv_guide))
        sections.add(Section("Devices", "3 Total", R.drawable.tv_guide))
        sections.add(Section("Devices", "3 Total", R.drawable.tv_guide))
        sections.add(Section("Devices", "3 Total", R.drawable.tv_guide))

        sectionAdapter = SectionAdapter(this, sections)
        apartment_sections.adapter = sectionAdapter
        apartment_sections.isExpanded = true


    }
}
