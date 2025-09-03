package io.voxkit.kopus

import kotlinx.cinterop.CPointer
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.*
import opus.OPUS_OK
import opus.opus_decode
import opus.opus_decoder_create

public actual fun Opus.decoder(
    sampleRate: SampleRate,
    channels: Channels
): OpusDecoder = OpusDecoderImpl(sampleRate, channels)

@OptIn(ExperimentalForeignApi::class)
private class OpusDecoderImpl(sampleRate: SampleRate, channels: Channels) : OpusDecoder {

    private var decoder: CPointer<cnames.structs.OpusDecoder>? = null

    init {
        val result = memScoped {
            val error = alloc<IntVar>()
            decoder = opus_decoder_create(
                Fs = sampleRate.value,
                channels = channels.value,
                error = error.ptr
            )
            error.value
        }

        require(result == OPUS_OK) {
            "Failed to create Opus decoder: ${Opus.getErrorString(result)}"
        }
    }

    override fun decode(
        data: ByteArray?,
        frameSize: Int,
        pcm: ShortArray,
        decodeFec: Boolean
    ): Int {
        return pcm.usePinned { pinnedPcm ->
            data?.usePinned { pinnedData ->
                opus_decode(
                    st = decoder,
                    data = pinnedData.addressOf(0).reinterpret(),
                    len = data.size,
                    pcm = pinnedPcm.addressOf(0),
                    frame_size = frameSize,
                    decode_fec = if (decodeFec) 1 else 0
                )
            } ?: opus_decode(
                st = decoder,
                data = null,
                len = 0,
                pcm = pinnedPcm.addressOf(0),
                frame_size = frameSize,
                decode_fec = if (decodeFec) 1 else 0
            )
        }
    }

    override fun decode(
        data: ByteArray?,
        frameSize: Int,
        pcm: FloatArray,
        decodeFec: Boolean
    ): Int {
        return pcm.usePinned { pinnedPcm ->
            data?.usePinned { pinnedData ->
                opus_decode(
                    st = decoder,
                    data = pinnedData.addressOf(0).reinterpret(),
                    len = data.size,
                    pcm = pinnedPcm.addressOf(0).reinterpret(),
                    frame_size = frameSize,
                    decode_fec = if (decodeFec) 1 else 0
                )
            } ?: opus_decode(
                st = decoder,
                data = null,
                len = 0,
                pcm = pinnedPcm.addressOf(0).reinterpret(),
                frame_size = frameSize,
                decode_fec = if (decodeFec) 1 else 0
            )
        }
    }

    override fun close() {
        decoder ?: return
        opus.opus_decoder_destroy(decoder)
        decoder = null
    }
}
