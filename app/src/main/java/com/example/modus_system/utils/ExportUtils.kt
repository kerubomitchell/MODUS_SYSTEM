package com.example.modus_system.utils

import android.content.Context
import android.content.Intent
import androidx.core.content.FileProvider
import com.example.modus_system.data.Transaction
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

object ExportUtils {

    fun exportTransactionsToCsv(
        context: Context,
        transactions: List<Transaction>,
        modusScore: Int,
        userName: String
    ) {
        val fileName = "Modus_Financial_Report_${System.currentTimeMillis()}.csv"
        val csvFile = File(context.cacheDir, fileName)

        try {
            csvFile.bufferedWriter().use { writer ->
                // Agent Executive Summary
                writer.write("MODUS BEHAVIORAL AGENT REPORT\n")
                writer.write("User: $userName\n")
                writer.write("Report Date: ${SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(Date())}\n")
                writer.write("Modus Score: $modusScore%\n")
                writer.write("Behavioral Verdict: ${getBehavioralVerdict(modusScore)}\n")
                writer.write("\n")

                // Column Headers
                writer.write("ID,Merchant,Amount,Currency,Category,Note,Date,Type\n")

                // Data Rows
                transactions.forEach { tx ->
                    val dateStr = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(Date(tx.timestamp))
                    val typeStr = if (tx.category == "IRON_SHIELD") "Survival" else "Growth"
                    writer.write("${tx.id},\"${tx.merchantName}\",${tx.amount},${tx.currency},${tx.category},\"${tx.note}\",$dateStr,$typeStr\n")
                }
            }

            shareFile(context, csvFile, "text/csv")

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun shareFile(context: Context, file: File, mimeType: String) {
        val uri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            file
        )

        val intent = Intent(Intent.ACTION_SEND).apply {
            type = mimeType
            putExtra(Intent.EXTRA_STREAM, uri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }

        context.startActivity(Intent.createChooser(intent, "Export Financial Report"))
    }

    private fun getBehavioralVerdict(score: Int): String {
        return when {
            score >= 70 -> "Optimal Velocity. You are aggressively building future wealth while maintaining a lean survival shield."
            score >= 50 -> "Balanced Growth. You have achieved Golden State stability. Keep prioritizing growth assets."
            score >= 30 -> "Defensive Stance. Your survival shield is secure, but consider shifting more capital to the Golden Path."
            else -> "Survival Mode. High defensive overhead. The Agent recommends auditing recurring shield costs to unlock growth."
        }
    }
}
