import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AudioFile
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.icons.filled.Image
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter // Cần thư viện Coil để load ảnh từ Uri
import com.example.app.viewmodel.SongViewModel

@Composable
fun UploadFileSong(
    songId: String, // ID của bài hát cần upload (lấy từ màn hình trước hoặc API tạo bài hát)
    viewModel: SongViewModel,
    //onUploadSuccess: () -> Unit
) {
    val context = LocalContext.current

    // 1. Khởi tạo State để lưu trữ URI tạm thời
    var imageUri by remember { mutableStateOf<Uri?>(null) }
    var audioUri by remember { mutableStateOf<Uri?>(null) }

    // 2. Định nghĩa Launcher cho Ảnh (MIME type: image/*)
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        imageUri = uri
    }

    // 3. Định nghĩa Launcher cho Audio (MIME type: audio/*)
    val audioPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        audioUri = uri
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(text = "Upload File cho Bài hát", style = MaterialTheme.typography.headlineSmall)

        // --- KHU VỰC CHỌN ẢNH (IMAGE PICKER) ---
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            elevation = CardDefaults.cardElevation(4.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (imageUri != null) {
                    // Hiển thị Preview ảnh đã chọn
                    Image(
                        painter = rememberAsyncImagePainter(imageUri),
                        contentDescription = "Selected Image",
                        modifier = Modifier
                            .size(150.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .border(1.dp, Color.Gray, RoundedCornerShape(8.dp)),
                        contentScale = ContentScale.Crop
                    )
                    Text(
                        text = "Đã chọn ảnh",
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                } else {
                    // Placeholder khi chưa chọn
                    Icon(
                        imageVector = Icons.Default.Image,
                        contentDescription = null,
                        modifier = Modifier.size(60.dp),
                        tint = Color.Gray
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Button(onClick = { imagePickerLauncher.launch("image/*") }) {
                    Text("Chọn Ảnh Bìa")
                }
            }
        }

        // --- KHU VỰC CHỌN NHẠC (AUDIO PICKER) ---
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            elevation = CardDefaults.cardElevation(4.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    imageVector = Icons.Default.AudioFile,
                    contentDescription = null,
                    modifier = Modifier.size(60.dp),
                    tint = if (audioUri != null) MaterialTheme.colorScheme.primary else Color.Gray
                )

                // Hiển thị đường dẫn (hoặc tên file)
                Text(
                    text = if (audioUri != null) "Đã chọn file nhạc" else "Chưa chọn file nhạc",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(vertical = 8.dp)
                )

                Button(onClick = { audioPickerLauncher.launch("audio/*") }) {
                    Text("Chọn File Nhạc (.mp3)")
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // --- NÚT UPLOAD CUỐI CÙNG ---
        Button(
            onClick = {
                if (imageUri != null && audioUri != null) {
                    // Gọi hàm trong ViewModel (đã viết ở bước trước)
                    // Lưu ý: Cần viết hàm uploadFiles trong ViewModel nhận vào context
                    viewModel.uploadFiles(songId, imageUri!!, audioUri!!, context)
                    Toast.makeText(context, "Upload thành công", Toast.LENGTH_SHORT).show()
                    imageUri = null
                    audioUri = null
                } else {
                    Toast.makeText(context, "Vui lòng chọn đủ cả ảnh và nhạc", Toast.LENGTH_SHORT).show()
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            enabled = imageUri != null && audioUri != null, // Chỉ enable khi đã chọn đủ
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
        ) {
            Icon(Icons.Default.CloudUpload, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Tiến hành Upload")
        }
    }
}