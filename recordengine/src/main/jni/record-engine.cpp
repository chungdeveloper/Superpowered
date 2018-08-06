#include <record-engine.h>
#include <media-engine.h>
#include <jni.h>
#include <stdlib.h>
#include <SLES/OpenSLES.h>
#include <SLES/OpenSLES_AndroidConfiguration.h>
#include <string.h>
#include <pthread.h>

//=====================================RECORD MODULE================================================
static RecordEngine *executeProcess = NULL;

static bool audioProcessing(void *__unused clientdata, short int *audioInputOutput,
                            int numberOfSamples, int __unused samplerate) {
    return executeProcess->process(audioInputOutput, (unsigned int) numberOfSamples,
                                   (unsigned int) samplerate);
}

// This is called periodically by the media server.
bool
RecordEngine::process(short int *audioInputOutput, unsigned int numberOfSamples,
                      unsigned int sampleRate) {
    SuperpoweredShortIntToFloat(audioInputOutput, inputBufferFloat, numberOfSamples);

//    // Input goes to the frequency domain.
//    frequencyDomain->addInput(inputBufferFloat, numberOfSamples);
//
//    // When FFT size is 2048, we have 1024 magnitude and phase bins
//    // in the frequency domain for every channel.
//    while (frequencyDomain->timeDomainToFrequencyDomain(magnitudeLeft, magnitudeRight, phaseLeft,
//                                                        phaseRight)) {
//        // You can work with frequency domain data from this point.
//
//        // This is just a quick example: we remove the magnitude of the first 20 bins,
//        // meaning total bass cut between 0-430 Hz.
//        memset(magnitudeLeft, 0, 24);
//        memset(magnitudeRight, 0, 24);
//
//        // We are done working with frequency domain data. Let's go back to the time domain.
//
//        // Check if we have enough room in the fifo buffer for the output.
//        // If not, move the existing audio data back to the buffer's beginning.
//        if (fifoOutputLastSample + stepSize >= fifoCapacity) {
//            // This will be true for every 100th iteration only,
//            // so we save precious memory bandwidth.
//            int samplesInFifo = fifoOutputLastSample - fifoOutputFirstSample;
//            if (samplesInFifo > 0)memmove(fifoOutput, fifoOutput + fifoOutputFirstSample * 2, samplesInFifo * sizeof(float) * 2);
//            fifoOutputFirstSample = 0;
//            fifoOutputLastSample = samplesInFifo;
//        };
//
//        // Transforming back to the time domain.
//        frequencyDomain->frequencyDomainToTimeDomain(magnitudeLeft, magnitudeRight, phaseLeft,
//                                                     phaseRight,
//                                                     fifoOutput + fifoOutputLastSample * 2);
//        frequencyDomain->advance();
//        fifoOutputLastSample += stepSize;
//    };
//
//    // If we have enough samples in the fifo output buffer, pass them to the audio output.
//    if (fifoOutputLastSample - fifoOutputFirstSample >= numberOfSamples) {
//    float *inputBuffer = fifoOutput + fifoOutputFirstSample * 2;
//        if (isRecording) {
//            SuperpoweredDeInterleave(inputBuffer, recordBufferFloat, recordBufferFloat,
//                                     numberOfSamples);
////            recorder->setSamplerate(sampleRate);
//            recorder->process(recordBufferFloat, numberOfSamples);
//        }
//    nBandEQ->process(inputBufferFloat, inputBufferFloat, numberOfSamples);
    threeBandEQ->process(inputBufferFloat, inputBufferFloat, numberOfSamples);
    reverb->process(inputBufferFloat, inputBufferFloat, numberOfSamples);
    echo->process(inputBufferFloat, inputBufferFloat, numberOfSamples);
    compressor->process(inputBufferFloat, inputBufferFloat, numberOfSamples);

    SuperpoweredFloatToShortInt(inputBufferFloat, audioInputOutput, numberOfSamples);
//        fifoOutputFirstSample += numberOfSamples;
    return isPlayback;
}

//else {
//return false;
//}
//}

void __unused RecordEngine::onSampleRecordListener(short *data, int sampleSize) {
    javaVM->AttachCurrentThread(&jniEnv, NULL);
    samples = jniEnv->NewShortArray(sampleSize);
    (jniEnv)->SetShortArrayRegion(samples, 0, sampleSize, data);
    (jniEnv)->CallVoidMethod(jGlobalObject, onRecordSampleListenerID, samples);
    javaVM->DetachCurrentThread();
}

#pragma clang diagnostic push
#pragma clang diagnostic ignored "-Wwritable-strings"
#pragma clang diagnostic ignored "-Wreturn-stack-address"


RecordEngine::RecordEngine(JNIEnv *env, jobject instance, const char *path, int bufferSize,
                           int sampleRate) {
    jniEnv = env;
    isPlayback = false;
    jObject = instance;
    targetClass = (jniEnv)->GetObjectClass(jObject);
    pathRecord = path;// "/storage/emulated/0/zalo/1520307248772.wav";// path;
    isRecording = false;
    onInitDoneMethodID = (jniEnv)->GetMethodID(targetClass, "onInitDoneListener", sigVoidIntMethod);
    (jniEnv)->GetJavaVM(&javaVM);
    jGlobalObject = jniEnv->NewGlobalRef(jObject);
    onRecordSampleListenerID = (jniEnv)->GetMethodID(targetClass, "onSampleRecordListener",
                                                     "([S)V");


    float tmpValue[] = {40.0f, 63.0f, 125.0f, 250.0f, 500.0f, 1000.0f, 2000.0f, 4000.0f, 8000.0f,
                        16000.0f, 0.0f};
    eqBandList = tmpValue;

    recorder = new SuperpoweredRecorder(pathRecord, (unsigned int) sampleRate, 0, 1, false,
                                        NULL, this);

    frequencyDomain = new SuperpoweredFrequencyDomain(
            FFT_LOG_SIZE); // This will do the main "magic".
    stepSize = frequencyDomain->fftSize / 4;

    compressor = new SuperpoweredCompressor((unsigned int) sampleRate);
    threeBandEQ = new Superpowered3BandEQ((unsigned int) sampleRate);
    echo = new SuperpoweredEcho((unsigned int) sampleRate);
    reverb = new SuperpoweredReverb((unsigned int) sampleRate);
    nBandEQ = new SuperpoweredNBandEQ((unsigned int) sampleRate, eqBandList);

    monoMixer = new SuperpoweredMonoMixer();

    recorder->setSamplerate((unsigned int) sampleRate);

    magnitudeLeft = (float *) malloc(frequencyDomain->fftSize * sizeof(float));
    magnitudeRight = (float *) malloc(frequencyDomain->fftSize * sizeof(float));
    phaseLeft = (float *) malloc(frequencyDomain->fftSize * sizeof(float));
    phaseRight = (float *) malloc(frequencyDomain->fftSize * sizeof(float));

    // Time domain result goes into a FIFO (first-in, first-out) buffer
    fifoOutputFirstSample = fifoOutputLastSample = 0;
    fifoCapacity = stepSize * 100;
    fifoOutput = (float *) malloc(fifoCapacity * sizeof(float) * 2 + 128);
    inputBufferFloat = (float *) malloc(bufferSize * sizeof(float) * 2 + 128);
    recordBufferFloat = (float *) malloc(bufferSize * sizeof(float) * 2 + 128);
    SuperpoweredCPU::setSustainedPerformanceMode(true);
    audioSystem = new SuperpoweredAndroidAudioIO(sampleRate, bufferSize, true, true,
                                                 audioProcessing, this, -1,
                                                 SL_ANDROID_STREAM_MEDIA,
                                                 bufferSize * 2);
    (jniEnv)->CallVoidMethod(jObject, onInitDoneMethodID);
}

#pragma clang diagnostic pop


void RecordEngine::setReverbParams(int param, float scaleValue) {
    reverb->enable(true);
    switch (param) {
        case REVERB_DRY:
            reverb->setDry(scaleValue);
            break;
        case REVERB_MIX:
            reverb->setMix(scaleValue);
            break;
        case REVERB_WIDTH:
            reverb->setWidth(scaleValue);
            break;
        case REVERB_ROOMSIZE:
            reverb->setRoomSize(scaleValue);
            break;
        case REVERB_WET:
            reverb->setWet(scaleValue);
            break;
        case REVERB_DAMP:
            reverb->setDamp(scaleValue);
            break;
        case REVERB_PREDELAY:
            reverb->setPredelay(scaleValue);
            break;
        case REVERB_LOW_CUT:
            reverb->setLowCut(scaleValue);
            break;
        default:
            break;
    }
}

RecordEngine::~RecordEngine() {
    delete audioSystem;
    delete recorder;
    delete reverb;
    delete echo;
    delete threeBandEQ;
    delete compressor;
    delete nBandEQ;
    free(recordBufferFloat);
    free(inputBufferFloat);
    delete pathRecord;
    free(magnitudeLeft);
    free(magnitudeRight);
    free(phaseLeft);
    free(phaseRight);
    free(fifoOutput);
//    delete frequencyDomain;
}

void RecordEngine::startRecord() {
    startRecordWithOffset(0);
}

void RecordEngine::startRecordWithOffset(int offset) {
    recorder->addToTracklist(RECORD_ART, RECORD_TITLE, offset, false);
    recorder->start(pathRecord);
    isRecording = true;
}

void RecordEngine::stopRecord() {
    if (!isRecording)
        return;
    recorder->stop();
    isRecording = false;
}

void
RecordEngine::onCompressorValue(float dryWetPercent, float ratio, float attack, float release,
                                float threshold, float hpCutOffHz) {
    compressor->enable(true);
    compressor->wet = dryWetPercent;
    compressor->ratio = ratio;
    compressor->attackSec = attack;
    compressor->releaseSec = release;
    compressor->thresholdDb = threshold;
    compressor->hpCutOffHz = hpCutOffHz;
}

void
RecordEngine::onEchoValue(float dry, float wet, float bpm, float beats, float decay, float mix) {
    echo->enable(true);
    echo->dry = dry;
    echo->wet = wet;
    echo->beats = beats;
    echo->bpm = bpm;
    echo->decay = decay;
    echo->setMix(mix);
}

void RecordEngine::onBandValues(float low, float mid, float high) {
    threeBandEQ->enable(true);
    threeBandEQ->bands[0] = low;
    threeBandEQ->bands[1] = mid;
    threeBandEQ->bands[2] = high;
}

#pragma clang diagnostic push
#pragma clang diagnostic ignored "-Wunused-parameter"

void RecordEngine::onProcessBandEQ(float value0, float value1, float value2, float value3,
                                   float value4, float value5, float value6, float value7,
                                   float value8, float value9) {
    nBandEQ->enable(true);
//    nBandEQ->setBand(0, value0);
//    nBandEQ->setBand(1, value1);
//    nBandEQ->setBand(2, value2);
//    nBandEQ->setBand(3, value3);
//    nBandEQ->setBand(4, value4);
//    nBandEQ->setBand(5, value5);
//    nBandEQ->setBand(6, value6);
//    nBandEQ->setBand(7, value7);
//    nBandEQ->setBand(8, value8);
//    nBandEQ->setBand(9, value9);
}

#pragma clang diagnostic pop

void RecordEngine::enableEffect(bool enable) {
    reverb->enable(enable);
    echo->enable(enable);
    nBandEQ->enable(enable);
    threeBandEQ->enable(enable);
    compressor->enable(enable);
}

void RecordEngine::startRecordPath(const char *path) {
    if (isRecording)
        return;
//    recorder->addToTracklist(RECORD_ART, RECORD_TITLE, 0, false);
    recorder->start(path);
    isRecording = true;
}

void RecordEngine::enablePlayback(bool enable) {
    isPlayback = enable;
}

void RecordEngine::enableCompressor(bool i) {
    compressor->enable(i);
}


//extern "C" JNIEXPORT void
//Java_vn_soft_dc_recordengine_RecorderEngine_FrequencyDomain(JNIEnv *__unused javaEnvironment,
//                                                            jobject __unused obj,
//                                                            jint samplerate,
//                                                            jint buffersize) {
//    executeProcess = new RecordEngine(buffersize, samplerate);
//}


#pragma clang diagnostic push
#pragma clang diagnostic ignored "-Wunused-parameter"
extern "C" JNIEXPORT void
Java_vn_soft_dc_recordengine_RecorderEngine_FrequencyDomainWithRecorder(JNIEnv *env,
                                                                        jobject instance,
                                                                        jstring path_,
                                                                        jint sampleRate,
                                                                        jint bufferSize) {
    const char *path = env->GetStringUTFChars(path_, 0);
    executeProcess = new RecordEngine(env, instance, path, bufferSize, sampleRate);
    env->ReleaseStringUTFChars(path_, path);
}
#pragma clang diagnostic pop

#pragma clang diagnostic push
#pragma clang diagnostic ignored "-Wunused-parameter"
extern "C" JNIEXPORT void JNICALL
Java_vn_soft_dc_recordengine_RecorderEngine_startRecordFile(JNIEnv *env, jobject instance) {

    executeProcess->startRecord();

}
#pragma clang diagnostic pop

#pragma clang diagnostic push
#pragma clang diagnostic ignored "-Wunused-parameter"
extern "C" JNIEXPORT void
Java_vn_soft_dc_recordengine_RecorderEngine_stopRecordFile(JNIEnv *env, jobject instance) {

    executeProcess->stopRecord();

}extern "C"
#pragma clang diagnostic pop
#pragma clang diagnostic push
#pragma clang diagnostic ignored "-Wunused-parameter"
JNIEXPORT void
Java_vn_soft_dc_recordengine_RecorderEngine_setEchoValue(JNIEnv *env, jobject instance,
                                                         jfloat dry, jfloat wet, jfloat bpm,
                                                         jfloat beats, jfloat decay,
                                                         jfloat mix) {
    executeProcess->onEchoValue(dry, wet, bpm, beats, decay, mix);
}extern "C"
#pragma clang diagnostic pop
#pragma clang diagnostic push
#pragma clang diagnostic ignored "-Wunused-parameter"
JNIEXPORT void
Java_vn_soft_dc_recordengine_RecorderEngine_onProcessBandEQ(JNIEnv *env, jobject instance,
                                                            jfloat value0, jfloat value1,
                                                            jfloat value2, jfloat value3,
                                                            jfloat value4, jfloat value5,
                                                            jfloat value6, jfloat value7,
                                                            jfloat value8, jfloat value9) {

    executeProcess->onProcessBandEQ(value0, value1, value2, value3,
                                    value4, value5, value6, value7,
                                    value8, value9);

}extern "C"
#pragma clang diagnostic pop
#pragma clang diagnostic push
#pragma clang diagnostic ignored "-Wunused-parameter"
JNIEXPORT void
Java_vn_soft_dc_recordengine_RecorderEngine_onBandValues(JNIEnv *env, jobject instance,
                                                         jfloat low, jfloat mid,
                                                         jfloat high) {
    executeProcess->onBandValues(low, mid, high);

}extern "C"
#pragma clang diagnostic pop
#pragma clang diagnostic push
#pragma clang diagnostic ignored "-Wunused-parameter"
JNIEXPORT void
Java_vn_soft_dc_recordengine_RecorderEngine_onCompressorValue(JNIEnv *env, jobject instance,
                                                              jfloat dryWetPercent,
                                                              jfloat ratio, jfloat attack,
                                                              jfloat release,
                                                              jfloat threshold,
                                                              jfloat hpCutOffHz) {

    executeProcess->onCompressorValue(dryWetPercent, ratio, attack, release,
                                      threshold, hpCutOffHz);

}extern "C"
#pragma clang diagnostic pop
#pragma clang diagnostic push
#pragma clang diagnostic ignored "-Wunused-parameter"
JNIEXPORT void
Java_vn_soft_dc_recordengine_RecorderEngine_onFxReverbValue(JNIEnv *env, jobject instance,
                                                            jint param, jfloat scaleValue) {

    executeProcess->setReverbParams(param, scaleValue);

}
#pragma clang diagnostic pop
extern "C"
#pragma clang diagnostic push
#pragma clang diagnostic ignored "-Wunused-parameter"
JNIEXPORT void
Java_vn_soft_dc_recordengine_RecorderEngine_enableEffect(JNIEnv *env, jobject instance,
                                                         jboolean enable) {

    executeProcess->enableEffect(enable);

}
#pragma clang diagnostic pop
extern "C"
#pragma clang diagnostic push
#pragma clang diagnostic ignored "-Wunused-parameter"
JNIEXPORT void
Java_vn_soft_dc_recordengine_RecorderEngine_startRecordWithOffset(JNIEnv *env,
                                                                  jobject instance,
                                                                  jint offset) {
    executeProcess->startRecordWithOffset(offset);
}
#pragma clang diagnostic pop
extern "C"
#pragma clang diagnostic push
#pragma clang diagnostic ignored "-Wunused-parameter"
JNIEXPORT void JNICALL
Java_vn_soft_dc_recordengine_RecorderEngine_startRecordFilePath(JNIEnv *env, jobject instance,
                                                                jstring pathTarget_) {
    const char *pathTarget = env->GetStringUTFChars(pathTarget_, false);
    executeProcess->startRecordPath(pathTarget);
    env->ReleaseStringUTFChars(pathTarget_, pathTarget);
}
#pragma clang diagnostic pop

extern "C"
#pragma clang diagnostic push
#pragma clang diagnostic ignored "-Wunused-parameter"
JNIEXPORT void
Java_vn_soft_dc_recordengine_RecorderEngine_release(JNIEnv *env, jobject instance) {
    if (executeProcess == NULL)
        return;
    executeProcess->~RecordEngine();
    executeProcess = NULL;
}
#pragma clang diagnostic pop

extern "C"
#pragma clang diagnostic push
#pragma clang diagnostic ignored "-Wunused-parameter"
JNIEXPORT void JNICALL
Java_vn_soft_dc_recordengine_RecorderEngine_enablePlayback(JNIEnv *env, jobject instance,
                                                           jboolean enable) {

    executeProcess->enablePlayback(enable);

}
#pragma clang diagnostic pop

extern "C"
#pragma clang diagnostic push
#pragma clang diagnostic ignored "-Wunused-parameter"
JNIEXPORT void
Java_vn_soft_dc_recordengine_RecorderEngine_onCompressEnable(JNIEnv *env, jobject instance,
                                                             jboolean b) {
    executeProcess->enableCompressor(b);
}
#pragma clang diagnostic pop