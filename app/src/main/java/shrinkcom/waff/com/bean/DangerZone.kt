package shrinkcom.waff.com.bean

import com.google.gson.annotations.SerializedName

public class DangerZone {

    @SerializedName("id")
     var id = 0

    @SerializedName("user_id")
    var userId = ""

    @SerializedName("latitude")
    var latitude = 0.0

    @SerializedName("longitude")
    var longitude = 0.0

    @SerializedName("city")
    var city = ""

    @SerializedName("dangers_id")
    var dangersId = 0


    @SerializedName("name")
    var dangersName = ""

    @SerializedName("status")
    var status = 0


    @SerializedName("image")
    var image = ""

}