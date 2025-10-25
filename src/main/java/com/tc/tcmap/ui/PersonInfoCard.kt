package com.tc.tcmap.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import coil3.compose.AsyncImage
import coil3.compose.rememberAsyncImagePainter
import com.tc.tcmap.R
import com.tc.tcmap.domain.PersonInfo

@Composable
fun PersonInfoCard(modifier: Modifier,
                   personInfo : PersonInfo,
                   onCardClicked : () -> Unit) {
    var errorInNetImg by remember { mutableStateOf(false) }
    val regularTextSize = 16.sp
    val smallTextSize = 14.sp
    Card(
        onClick = {
            onCardClicked()
        },
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        ConstraintLayout(Modifier.fillMaxWidth()) {
            val (imgRef, titleRef, professionRef, divRef, starRef, rateRef, vRef, disRef) = createRefs()

            if (!errorInNetImg) {
                AsyncImage(
                    personInfo.imageUrl,
                    contentDescription = "",
                    onError = { errorInNetImg = true },
                    modifier = Modifier
                        .size(60.dp) // Set the desired size
                        .clip(CircleShape) // Apply the circular shape
                        .background(Color.White)
                        .constrainAs(imgRef) {
                            top.linkTo(titleRef.top, margin = (-8).dp)

                            start.linkTo(parent.start, margin = 16.dp)
                        }
                )
            } else {
                Image(
                    painter = rememberAsyncImagePainter(R.drawable.star),
                    contentDescription = "",
                    modifier = Modifier
                        .size(60.dp) // Set the desired size
                        .clip(CircleShape) // Apply the circular shape
                        .background(Color.White)
                        .constrainAs(imgRef) {
                            top.linkTo(titleRef.top, margin = (-8).dp)
                            start.linkTo(parent.start, margin = 16.dp)
                        }
                )
            }


            Text(
                personInfo.title, fontSize = regularTextSize,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.constrainAs(titleRef) {
                    top.linkTo(parent.top, margin = 20.dp)
                    start.linkTo(imgRef.end, margin = 16.dp)
                    end.linkTo(parent.end, margin = 12.dp)
                    width = Dimension.fillToConstraints
                })

            Text(
                personInfo.profession,
                fontSize = smallTextSize,
                color = Color(0xFFA9B8C1),
                modifier = Modifier.constrainAs(professionRef) {
                    top.linkTo(titleRef.bottom, margin = 2.dp)
                    start.linkTo(imgRef.end, margin = 16.dp)
                    end.linkTo(parent.end, margin = 12.dp)
                    width = Dimension.fillToConstraints
                })

            HorizontalDivider(
                color = Color(0xFFA6AAB4),
                modifier = Modifier.constrainAs(divRef) {
                    top.linkTo(professionRef.bottom, margin = 20.dp)
                    start.linkTo(parent.start, margin = 0.dp)
                    end.linkTo(parent.end, margin = 0.dp)
                    width = Dimension.fillToConstraints
                })



            VerticalDivider(
                color = Color(0xFFA6AAB4),
                modifier = Modifier.constrainAs(vRef) {
                    top.linkTo(divRef.bottom)
                    bottom.linkTo(parent.bottom)
                    centerHorizontallyTo(parent)
                    height = Dimension.fillToConstraints
                })

            Icon(
                painter = painterResource(R.drawable.star),
                contentDescription = "",
                tint = Color(0xFFFF7A1A),
                modifier = Modifier.constrainAs(starRef) {
                    top.linkTo(disRef.top, margin = 0.dp)
                    bottom.linkTo(disRef.bottom, margin = 0.dp)
                    start.linkTo(parent.start, margin = 24.dp)
                })

            Text(
                personInfo.rating.toString(),
                fontSize = smallTextSize,
                modifier = Modifier.constrainAs(rateRef) {
                    top.linkTo(disRef.top, margin = 0.dp)
                    bottom.linkTo(disRef.bottom, margin = 0.dp)
                    start.linkTo(starRef.end, margin = 8.dp)
                })



            ConstraintLayout(
                Modifier
                    .wrapContentSize()
                    .constrainAs(disRef) {
                        top.linkTo(divRef.bottom, margin = 12.dp)
                        bottom.linkTo(parent.bottom, margin = 12.dp)
                        end.linkTo(parent.end, margin = 24.dp)
                    }) {
                val (tv1, tv2) = createRefs()
                Text(
                    "${personInfo.distance} Miles",
                    fontWeight = FontWeight.Light,
                    fontSize = smallTextSize, modifier = Modifier.constrainAs(tv1) {
                        top.linkTo(parent.top, margin = 0.dp)
                        start.linkTo(parent.start, margin = 0.dp)
                    })
                Text(
                    personInfo.proximity,
                    fontWeight = FontWeight.Light,
                    color = Color(0xFFA9B8C1),
                    fontSize = smallTextSize, modifier = Modifier.constrainAs(tv2) {
                        top.linkTo(tv1.bottom, margin = 0.dp)
                        start.linkTo(parent.start, margin = 0.dp)
                    })
            }

        }
    }
}
