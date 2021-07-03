package dotd.hmp.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.fragment.app.Fragment
import dotd.hmp.R
import dotd.hmp.data.Model
import dotd.hmp.databinding.ActivityViewDataModelBinding
import dotd.hmp.fragment.ViewDataModelFragment

class ViewDataModelActivity : AppCompatActivity() {
    private val b by lazy { ActivityViewDataModelBinding.inflate(layoutInflater) }
    val model: Model by lazy { intent.getSerializableExtra("model") as Model }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(b.root)

        supportFragmentManager.beginTransaction()
            .replace(R.id.container, ViewDataModelFragment())
            .commit()

    }

    fun addFragment(fragment: Fragment) =
        supportFragmentManager.beginTransaction().add(R.id.container, fragment).commit()


    fun removeFragment(fragment: Fragment) =
        supportFragmentManager.beginTransaction().remove(fragment).commit()
}
