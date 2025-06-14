/**
 * TransactionAdapter.kt
 *
 *
 *
 *
 *  Assistance provided by ChatGPT, OpenAI (2025). https://chat.openai.com
 */

package com.example.moneymanager

import android.content.ActivityNotFoundException
import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.core.content.ContextCompat.startActivity
import androidx.core.content.FileProvider
import java.io.File


class TransactionAdapter(
    private val context: Context,
    private val transactions: List<UserTransaction>

): RecyclerView.Adapter<TransactionAdapter.TransactionViewHolder>() {

    inner class TransactionViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val cardView: CardView = view.findViewById(R.id.cardView)
        val tvAmount: TextView = view.findViewById(R.id.tvAmount)
        val tvDate: TextView = view.findViewById(R.id.tvDate)
        val tvCategory: TextView = view.findViewById(R.id.tvCategory)
        val tvDescription: TextView = view.findViewById(R.id.tvDescription)
        val btnVAttachment: Button = view.findViewById(R.id.btnViewAttachment)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransactionViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.transaction_item, parent, false)
        return TransactionViewHolder(view)
    }

    override fun getItemCount(): Int = transactions.size

    override fun onBindViewHolder(holder: TransactionViewHolder, position: Int){
        val transaction = transactions[position]
        holder.tvAmount.text = transaction.amount.toString()
        holder.tvDate.text = transaction.date
        holder.tvCategory.text = transaction.category
        //Setting the transaction card colour to green or red depending on the type of transaction
        val cardColour = if (transaction.type.equals("income", ignoreCase = true))
            android.graphics.Color.parseColor("#77d46c")
        else
            android.graphics.Color.parseColor("#ff6666")

        holder.cardView.setCardBackgroundColor(cardColour)

        if (transaction.description.isNotBlank()) {
            holder.tvDescription.visibility = View.VISIBLE
            holder.tvDescription.text = transaction.description
        } else {
            holder.tvDescription.visibility = View.GONE
        }

        //Allowing the user to view attachments
        if (transaction.attachment.isNotBlank()) {
            holder.btnVAttachment.visibility = View.VISIBLE

            holder.btnVAttachment.setOnClickListener {

                val attachmentFile = File(transaction.attachment)
                if (attachmentFile.exists()) {
                    val fileUri = FileProvider.getUriForFile(
                        context,
                        "${context.packageName}.provider",
                        attachmentFile
                    )

                    val intent = Intent(Intent.ACTION_VIEW)
                    intent.setDataAndType(fileUri, context.contentResolver.getType(fileUri))
                    intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)

                    try {
                        context.startActivity(intent)
                    } catch (e: ActivityNotFoundException) {
                        Toast.makeText(context, "No app found to open this file", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(context, "Attachment not found", Toast.LENGTH_SHORT).show()
                }
            }
        } else {
            holder.btnVAttachment.visibility = View.GONE
        }
    }

}

// ============================== End of file ==============================