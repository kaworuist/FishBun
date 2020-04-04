package com.sangcomz.fishbun.ui.picker.model

import android.net.Uri
import com.sangcomz.fishbun.MimeType
import com.sangcomz.fishbun.datasource.FishBunDataSource
import com.sangcomz.fishbun.datasource.ImageDataSource
import com.sangcomz.fishbun.ui.picker.model.PickerRepository
import java.util.concurrent.Future

class PickerRepositoryImpl(
    private val imageDataSource: ImageDataSource,
    private val fishBunDataSource: FishBunDataSource
) :
    PickerRepository {
    override fun getAllMediaThumbnailsPath(bucketId: Long): Future<List<Uri>> {
        return imageDataSource.getAllMediaThumbnailsPath(
            bucketId,
            fishBunDataSource.getExceptMimeTypeList(),
            fishBunDataSource.getSpecifyFolderList()
        )
    }

    override fun getDirectoryPath(bucketId: Long): Future<String> {
        return imageDataSource.getDirectoryPath(bucketId)
    }

}