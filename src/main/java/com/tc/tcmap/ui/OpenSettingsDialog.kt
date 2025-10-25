package com.tc.tcmap.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension

@Composable
fun OpenSettingsDialog(isDialogShown : Boolean, hideDialog : () -> Unit, ok : () -> Unit){
    if(isDialogShown){
        Dialog(onDismissRequest = {
            hideDialog()
        }) {
            Card(Modifier.fillMaxWidth()) {
                ConstraintLayout(Modifier.fillMaxWidth()) {
                    val (titleRef, descriptionRef, ooptionRef) = createRefs()

                    Text("Open Settings",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.constrainAs(titleRef){
                            top.linkTo(parent.top, margin = 16.dp)
                            start.linkTo(parent.start, margin = 16.dp)
                            end.linkTo(parent.end, margin = 16.dp)
                            width = Dimension.fillToConstraints
                        })

                    Text("You will have to enable the location settings for precise location.",
                        modifier = Modifier.constrainAs(descriptionRef){
                            top.linkTo(titleRef.bottom, margin = 12.dp)
                            start.linkTo(parent.start, margin = 16.dp)
                            end.linkTo(parent.end, margin = 16.dp)
                            width = Dimension.fillToConstraints
                        })

                    Column(verticalArrangement = Arrangement.spacedBy(4.dp),
                        modifier = Modifier.constrainAs(ooptionRef){
                            top.linkTo(descriptionRef.bottom, margin = 16.dp)
                            start.linkTo(parent.start, margin = 16.dp)
                            end.linkTo(parent.end, margin = 16.dp)
                            bottom.linkTo(parent.bottom, margin = 16.dp)
                            width = Dimension.fillToConstraints
                        }) {

                        Button(onClick = {
                            ok()
                        }, modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16)
                        ) {
                            Text("Go To Settings")
                        }
                        Button(onClick = {
                            hideDialog()
                        }, modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16),
                            colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent)) {
                            Text("Cancel", color = Color.Gray)
                        }

                    }

                }
            }
        }
    }

}