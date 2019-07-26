package shrinkcom.waff.com.bean

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

class UserData() : Parcelable
{
    override fun writeToParcel(dest: Parcel?, flags: Int) {
        dest?.writeString(userId)
        dest?.writeString(username)
        dest?.writeString(email)
        dest?.writeString(password)
        dest?.writeString(mobile)
        dest?.writeString(image)
        dest?.writeString(avatar)
        dest?.writeString(otp)
        dest?.writeString(isExpired)
        dest?.writeString(createAt)
        dest?.writeString(verifyStatus)
    }
    constructor(parcel: Parcel) : this() {
        userId = parcel.readString()
        username = parcel.readString()
        email = parcel.readString()
        password = parcel.readString()
        mobile = parcel.readString()
        image = parcel.readString()
        avatar = parcel.readString()
        otp = parcel.readString()
        isExpired = parcel.readString()
        createAt = parcel.readString()
        verifyStatus = parcel.readString()

    }

    @SerializedName("user_id")
    var userId:String = "";

    @SerializedName("username")
    var username:String = ""

    @SerializedName("email")
    var email:String = ""

    @SerializedName("password")
    var password:String = ""

    @SerializedName("mobile")
    var mobile:String = ""

    @SerializedName("image")
    var image:String = ""

    @SerializedName("avatar")
    var avatar:String = ""

    @SerializedName("otp")
    var otp:String = ""

    @SerializedName("is_expired")
    var isExpired:String = ""

    @SerializedName("create_at")
    var  createAt:String = ""


    @SerializedName("outside_action")
    var  outsideAction:String = ""




    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<UserData> {
        override fun createFromParcel(parcel: Parcel): UserData {
            return UserData(parcel)
        }

        override fun newArray(size: Int): Array<UserData?> {
            return arrayOfNulls(size)
        }
    }

    @SerializedName("verify_status")
    var verifyStatus :String = ""
}