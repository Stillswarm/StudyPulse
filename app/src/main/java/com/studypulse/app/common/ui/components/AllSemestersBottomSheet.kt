package com.studypulse.app.common.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.studypulse.app.common.util.convertToSentenceCase
import com.studypulse.app.feat.semester.domain.model.Semester

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AllSemestersBottomSheet(
    sheetState: SheetState,
    semesterList: List<Semester>,
    onSemesterClick: (Semester) -> Unit,
    onDismiss: () -> Unit,
    onAddSemester: () -> Unit,
    selectedSemesterId: String,
    buttonColor: Color,
) {
    if (sheetState.isVisible) {
        ModalBottomSheet(
            onDismissRequest = { onDismiss() },
            dragHandle = {},
            shape = RectangleShape,
        ) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Select Semester",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.SemiBold
                )

                Button(
                    onClick = onAddSemester,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = buttonColor
                    ),
                    shape = RectangleShape
                ) {
                    Text(
                        text = "+ Add New",
                        fontSize = 14.sp,
                    )
                }
            }
            semesterList.forEach { semester ->
                SemesterBottomSheetItem(
                    selectedSemester = selectedSemesterId,
                    semester = semester,
                    onSemesterClick = { onSemesterClick(it) },
                    buttonColor = buttonColor,
                )
            }
        }
    }
}

@Composable
fun SemesterBottomSheetItem(
    selectedSemester: String,
    semester: Semester,
    onSemesterClick: (Semester) -> Unit,
    buttonColor: Color,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp)
            .clickable { onSemesterClick(semester) },
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = "${semester.name.name.convertToSentenceCase()} Semester, Year ${semester.year}",
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold,
                lineHeight = 24.sp,
            )

            Text(
                text = "${semester.startDate} - ${semester.endDate}",
                fontSize = 14.sp,
                lineHeight = 20.sp,
            )
        }
        RadioButton(
            selected = semester.id == selectedSemester,
            onClick = {  },     // outer box is clickable
            colors = RadioButtonDefaults.colors(
                selectedColor = buttonColor
            )
        )
    }
}