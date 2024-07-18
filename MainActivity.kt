package com.familycards.test

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.ContactsContract
import android.provider.ContactsContract.PhoneLookup
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.familycards.test.ui.theme.FamilyCardsTestTaskTheme


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            FamilyCardsTestTaskTheme {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Button(modifier = Modifier.size(200.dp),
                        onClick = { callToSimon("+4915258703689") })
                    { Text("Call Simon") }
                }
            }
        }
    }


    private fun callToSimon(phoneNumber: String) {
        val mimeString = "vnd.android.cursor.item/vnd.com.whatsapp.voip.call"
        val intent = Intent()
        intent.setAction(Intent.ACTION_VIEW)
        val name = getContactName(phoneNumber, this@MainActivity)
        val whatsappContactId = getContactIdForWhatsAppCall(name)
        if (whatsappContactId != 0) {
            intent.setDataAndType(
                Uri.parse("content://com.android.contacts/data/$whatsappContactId"),
                mimeString
            )
            intent.setPackage("com.whatsapp")
            startActivity(intent)
        }
    }

    private fun getContactIdForWhatsAppCall(name: String): Int {
        val cursor = contentResolver.query(
            ContactsContract.Data.CONTENT_URI,
            arrayOf(ContactsContract.Data._ID),
            ContactsContract.Data.DISPLAY_NAME + "=? and " + ContactsContract.Data.MIMETYPE + "=?",
            arrayOf(name, "vnd.android.cursor.item/vnd.com.whatsapp.voip.call"),
            ContactsContract.Contacts.DISPLAY_NAME
        )

        if (cursor!!.count > 0) {
            cursor.moveToNext()
            val phoneContactID: Int =
                cursor.getInt(cursor.getColumnIndex(ContactsContract.Data._ID))
            return phoneContactID
        } else {
            return 0
        }
    }

    private fun getContactName(phoneNumber: String?, context: Context): String {
        val uri = Uri.withAppendedPath(PhoneLookup.CONTENT_FILTER_URI, Uri.encode(phoneNumber))

        val projection = arrayOf(PhoneLookup.DISPLAY_NAME)

        var contactName = ""
        val cursor = context.contentResolver.query(uri, projection, null, null, null)

        if (cursor != null) {
            if (cursor.moveToFirst()) {
                contactName = cursor.getString(0)
            }
            cursor.close()
        }

        return contactName
    }

}




