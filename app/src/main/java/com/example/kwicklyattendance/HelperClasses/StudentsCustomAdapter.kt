package com.example.kwicklyattendance.HelperClasses

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.kwicklyattendance.Interfaces.studentsClickInterface
import com.example.kwicklyattendance.R

class studentsCustomAdapter(private val mList: List<studentsItemsVM>, val listener : studentsClickInterface): RecyclerView.Adapter<studentsCustomAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        // inflates the card_view_design view
        // that is used to hold list item
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.student_card_view_design, parent, false)

        return ViewHolder(view)


//        val itemBinding = ActivityAttendanceDetailsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
//        return ViewHolder(itemBinding)

    }

    // binds the list items to a view
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val studentsItemsVM = mList[position]

        // sets the image to the imageview from our itemHolder class

        // sets the text to the textview from our itemHolder class
        holder.crdEmail.text = studentsItemsVM.studentEmail
        holder.bind(mList[position], listener)

    }

    // return the number of the items in the list
    override fun getItemCount(): Int {
        return mList.size
    }
//recyclerview adapter for the student display on adminhome
    class ViewHolder(ItemView: View) : RecyclerView.ViewHolder(ItemView) {

        val crdEmail: TextView = itemView.findViewById(R.id.crdVwEmailLabel)


        fun bind(student : studentsItemsVM, listener:studentsClickInterface){
            crdEmail.text = "${student.studentFirstName} ${student.studentLastName}   Email: ${student.studentEmail}"
            crdEmail.setOnClickListener{
                listener.onItemClick(student)
            }
        }

    }

}