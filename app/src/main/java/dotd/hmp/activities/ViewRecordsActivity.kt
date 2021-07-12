package dotd.hmp.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import dotd.hmp.R
import dotd.hmp.data.Model
import dotd.hmp.data.ModelDB
import dotd.hmp.databinding.ActivityViewRecordsBinding
import dotd.hmp.fragment.ViewRecordsFragment

/*
    @AddRecordFragment
    @ViewRecordFragment
 */
class ViewRecordsActivity : AppCompatActivity() {
    private val b by lazy { ActivityViewRecordsBinding.inflate(layoutInflater) }
    val model: MutableLiveData<Model> by lazy { MutableLiveData<Model>() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(b.root)
        model.value = ModelDB.getModel(intent.getIntExtra("model_id", -1))

        supportFragmentManager.beginTransaction()
            .setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right)
            .replace(R.id.container, ViewRecordsFragment())
            .commit()

    }

    fun addFragment(fragment: Fragment, nameAddbackStack: String) =
        supportFragmentManager.beginTransaction()
            .setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right)
            .add(R.id.container, fragment)
            .addToBackStack(nameAddbackStack)
            .commit()


    fun removeFragment(fragment: Fragment) =
        supportFragmentManager.beginTransaction()
            .setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right)
            .remove(fragment)
            .commit()

    override fun onBackPressed() {
        if (supportFragmentManager.backStackEntryCount == 0) {
            super.onBackPressed()
        } else {
            supportFragmentManager.popBackStack()
        }

    }
}
