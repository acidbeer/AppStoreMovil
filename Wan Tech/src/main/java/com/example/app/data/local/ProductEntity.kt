package com.example.app.data.local

import android.os.Parcel
import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "products")
data class ProductEntity(
    @PrimaryKey val id: Int,
    val name: String,
    val imageUrl: String,
    val price: String,
    val description: String,
    val isInCart: Boolean = false, // para distinguir compra o carrito
    var quantity: Int = 1 // Campo de cantidad
) : Parcelable {

    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readByte() != 0.toByte() // Para el valor Booleano
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(id)
        parcel.writeString(name)
        parcel.writeString(imageUrl)
        parcel.writeString(price)
        parcel.writeString(description)
        parcel.writeByte(if (isInCart) 1 else 0) // Convertir Booleano a Byte
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<ProductEntity> {
        override fun createFromParcel(parcel: Parcel): ProductEntity {
            return ProductEntity(parcel)
        }

        override fun newArray(size: Int): Array<ProductEntity?> {
            return arrayOfNulls(size)
        }
    }
}
