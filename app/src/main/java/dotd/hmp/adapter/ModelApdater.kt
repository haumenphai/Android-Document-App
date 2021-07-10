package dotd.hmp.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import dotd.hmp.R
import dotd.hmp.data.Model
import dotd.hmp.databinding.ItemModelBinding

class ModelApdater(private var list: List<Model> = mutableListOf()): RecyclerView.Adapter<ModelApdater.ModelHolder>() {

    @JvmName("setList1")
    fun setList(list: List<Model>) {
        this.list = list
        notifyDataSetChanged()
    }

    fun getList(): List<Model> = list

    fun selectAll() {
        list.forEach { it.isSelected = true }
        notifyDataSetChanged()
    }

    fun unSelectAll() {
        list.forEach { it.isSelected = false }
        notifyDataSetChanged()
    }

    fun getItemSelected() = list.filter { it.isSelected }



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ModelHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_model, parent, false)
        return ModelHolder(view)
    }

    override fun onBindViewHolder(holder: ModelHolder, position: Int) {
        val b = holder.b
        val model = list[position]

        if (model.hasIcon())
            b.icon.setImageResource(model.icon!!)
        else
            b.icon.setImageResource(R.drawable.ic_default_model_icon)


        if (model.isSelected) {
            b.background.setBackgroundColor(Color.parseColor("#ffd4df"))
            b.icon.background = null
        } else {
            b.background.setBackgroundResource(R.drawable.rippler_blue_white)
            if (model.hasIcon())
                b.icon.setImageResource(model.icon!!)
            else
                b.icon.setImageResource(R.drawable.ic_default_model_icon)
        }





        b.modelName.text = model.name
    }

    override fun getItemCount(): Int = list.size


    lateinit var onClickItem: (model: Model) -> Unit
    lateinit var onLongClickItem: (model: Model) -> Unit

    inner class ModelHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val b: ItemModelBinding = ItemModelBinding.bind(itemView)
        init {
            b.background.setOnClickListener {
                onClickItem(list[layoutPosition])
            }
            b.background.setOnLongClickListener {
                onLongClickItem(list[layoutPosition])
                true
            }
        }
    }
}
