package test.compose.zingplayer.api

import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.JsonReader
import com.squareup.moshi.JsonWriter
import com.squareup.moshi.Moshi
import com.squareup.moshi.internal.Util
import kotlin.reflect.KClass

data class GetResponse<T: Any>(
    val err: Int,
    val msg: String,
    val data: T,
) {
    class Adapter<T: Any>(
        moshi: Moshi,
        dataClass: KClass<T>,
    ): JsonAdapter<GetResponse<T>>() {
        private val options: JsonReader.Options = JsonReader.Options.of("err", "msg", "data")
        private val intAdapter = moshi.adapter<Int>(Int::class.java)
        private val stringAdapter = moshi.adapter<String>(String::class.java)
        private val dataAdapter = moshi.adapter<T>(dataClass.java)

        override fun fromJson(reader: JsonReader): GetResponse<T> {
            var err: Int? = null
            var msg: String? = null
            var data: T? = null
            reader.beginObject()
            while (reader.hasNext()) {
                when (reader.selectName(options)) {
                    0 -> err = intAdapter.fromJson(reader) ?: throw Util.unexpectedNull("err", "err", reader)
                    1 -> msg = stringAdapter.fromJson(reader) ?: throw Util.unexpectedNull("msg", "msg", reader)
                    2 -> data = dataAdapter.fromJson(reader) ?: throw Util.unexpectedNull("data", "data", reader)
                    -1 -> {
                        reader.skipName()
                        reader.skipValue()
                    }
                }
            }
            reader.endObject()
            return GetResponse<T>(
                err = err ?: throw Util.missingProperty("err", "err", reader),
                msg = msg ?: throw Util.missingProperty("msg", "msg", reader),
                data = data ?: throw Util.missingProperty("data", "data", reader),
            )
        }

        override fun toJson(writer: JsonWriter, value: GetResponse<T>?) {
            if (value == null) {
                throw NullPointerException("value_ was null! Wrap in .nullSafe() to write nullable values.")
            }
            writer.beginObject()
            writer.name("err")
            intAdapter.toJson(writer, value.err)
            writer.name("msg")
            stringAdapter.toJson(writer, value.msg)
            writer.name("data")
            dataAdapter.toJson(writer, value.data)
            writer.endObject()
        }
    }
}