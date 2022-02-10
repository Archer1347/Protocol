package com.example.modulea

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.FragmentActivity
import com.protocol.core.ProtocolFactory

class AModuleActivity : FragmentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_a)

        findViewById<View>(R.id.button).setOnClickListener {
            Toast.makeText(this, ProtocolFactory.getInstance().invoke(TestProtocol::class.java).data, Toast.LENGTH_SHORT).show()
        }
    }
}