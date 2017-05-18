#include <jni.h>
#include <stdlib.h>
#include <stdio.h>
#include <time.h>
#include <sys/types.h>
#include <sys/stat.h>
#include <string.h>
#include <unistd.h>

#include "sha256.h"

#define DEBUG 1

#define TAG "security_native"


#if DEBUG
#include <android/log.h>
#define V(x...) __android_log_print(ANDROID_LOG_VERBOSE, TAG, x)
#define I(x...) __android_log_print(ANDROID_LOG_INFO, TAG, x)
#define D(x...) __android_log_print(ANDROID_LOG_DEBUG, TAG, x)
#define W(x...) __android_log_print(ANDROID_LOG_WARN, TAG, x)
#define E(x...) __android_log_print(ANDROID_LOG_ERROR, TAG, x)
#else
#define V(...) do{} while(0)
#define I(...) do{} while(0)
#define D(...) do{} while(0)
#define W(...) do{} while(0)
#define E(...) do{} while(0)
#endif

#define GET_SIGNATURES 0x00000040

#define SHA_256_LEN 32

#define DES_KEY_LEN 24


char * VALID_PACAKGE_NAMES[] = {"com.ymnet.onekeyclean"};
char DEBUG_SIGNATRUE_SHA256[] = {0x14,0xfd,0xdc,0x9b,0x72,0xbf,0x71,0x30,0x5a,0x39,0xb8,
                                 0xc9,0x84,0xdb,0xd9,0x79,0xf3,0x59,0xda,0x29,0x08,0xcb,
                                 0x25,0x16,0x27,0xe9,0xd4,0x48,0xc8,0x01,0xba,0xdb};

char RELEASAE_SIGNATRUE_SHA256[] = {0x90,0x69,0xc9,0x09,0xd5,0x4a,0xcb,0xee,0x81,
                                    0x9b,0x48,0x7f,0x30,0x69,0xf8,0x17,0xd4,0x9a,
                                    0x7c,0xfe,0x7d,0xf6,0x8b,0x50,0x03,0xdd,0x6b,
                                    0xc0,0x74,0xa4,0xf1,0x6f};

time_t keyword = 0;

inline int isEquals(char* l, char* r, int len)
{
	int i = 0;
	for(i = 0; i < len; i++) {
		if(l[i] != r[i]){
			return 0;
		}
	}
	return 1;
}

inline unsigned char* sha256(char* input, int len)
{
	sha256_context ctx;
    unsigned char *sha256sum = (unsigned char*)malloc(sizeof(char) * SHA_256_LEN);
    sha256_starts(&ctx);
    sha256_update(&ctx, input, strlen(input));
    sha256_finish(&ctx, sha256sum);
    return sha256sum;
}

inline const char* getPackageName(JNIEnv *env, jobject context)
{
	jclass contextClass = (*env)->GetObjectClass(env, context);
	if(error_check(env) || contextClass == NULL){
		return NULL;
	}

	jmethodID getPackageNameMethodID = (*env)->GetMethodID(env, contextClass, "getPackageName", "()Ljava/lang/String;");
	if(error_check(env)){
		return NULL;
	}

	jstring packageName = (*env)->CallObjectMethod(env, context, getPackageNameMethodID);
	if(packageName == NULL){
		return NULL;
	}

	const char* packageNameChar = (*env)->GetStringUTFChars(env, packageName, 0);
	return packageNameChar;
}
char* getSignature(JNIEnv *env, jobject context, int* len);
inline char* getSignature(JNIEnv *env, jobject context, int* len)
{
	jclass contextClass = (*env)->GetObjectClass(env, context);
	if(error_check(env) || contextClass == NULL){
		return NULL;
	}

	jmethodID getPackageNameMethodID = (*env)->GetMethodID(env, contextClass, "getPackageName", "()Ljava/lang/String;");
	if(error_check(env)){
		return NULL;
	}

	jstring packageName = (*env)->CallObjectMethod(env, context, getPackageNameMethodID);
	if(packageName == NULL){
		return NULL;
	}

	const char* packageNameChar = (*env)->GetStringUTFChars(env, packageName, 0);

	jmethodID getPackageManagerMethodID = (*env)->GetMethodID(env, contextClass, "getPackageManager", "()Landroid/content/pm/PackageManager;");
	if(error_check(env)){
		return NULL;
	}

	jobject packageManager = (*env)->CallObjectMethod(env, context, getPackageManagerMethodID);
	jclass packageManagerClass = (*env)->GetObjectClass(env, packageManager);


	jmethodID getPackageInfoMethodID = (*env)->GetMethodID(env, packageManagerClass, "getPackageInfo", "(Ljava/lang/String;I)Landroid/content/pm/PackageInfo;");
	if(error_check(env)){
		return NULL;
	}

	jobject packageInfo = (*env)->CallObjectMethod(env, packageManager, getPackageInfoMethodID, packageName, GET_SIGNATURES);
	if(packageInfo == NULL){
		return NULL;
	}

	jclass packageInfoClass = (*env)->GetObjectClass(env, packageInfo);
	jfieldID signatureFieldID = (*env)->GetFieldID(env, packageInfoClass, "signatures", "[Landroid/content/pm/Signature;");
	jobjectArray signatures = (*env)->GetObjectField(env, packageInfo, signatureFieldID);
	int size = (*env)->GetArrayLength(env, signatures);
	if(size == 0){
		return NULL;
	}

	jobject signature = (*env)->GetObjectArrayElement(env, signatures, 0);
	jclass signatureClass = (*env)->GetObjectClass(env, signature);
	jmethodID toByteArrayMethodID = (*env)->GetMethodID(env, signatureClass, "toByteArray", "()[B");
	if(error_check(env)){
		return NULL;
	}
	jbyteArray signatureByteArray= (*env)->CallObjectMethod(env, signature, toByteArrayMethodID);
	int byteArrayLen = (*env)->GetArrayLength(env, signatureByteArray);
	char* signatureBytes = (char*)malloc(byteArrayLen);
	(*env)->GetByteArrayRegion(env, signatureByteArray, 0, byteArrayLen, signatureBytes);
	*len = byteArrayLen;
	return signatureBytes;
}
inline char* printHex(char* digits, int len)
{
    int i;
    char* str;

    str = malloc(len * 2 + 1);
    memset(str, 0, len * 2 + 1);
    for(i = 0; i < len; ++i)
    {
        char temp[3];
        sprintf(temp, "%02x", digits[i]);
        strcat(str, temp);
    }
//    printf("%s\n", str);
    return str;
}
int isValidPackage(JNIEnv *env, jobject context);
inline int isValidPackage(JNIEnv *env, jobject context)
{
    D("isValidPackage1");
	const char* packageNameChar = getPackageName(env, context);
	D("isValidPackage2");

	if(packageNameChar == NULL){
	    D("package name check failed");
		return 0;
	}

	//package name validation
	int length = sizeof(VALID_PACAKGE_NAMES) / sizeof(VALID_PACAKGE_NAMES[0]);
	int index = 0;
	for(index=0;index<length;index++){
		if(index==(length-1)){

			if(strcmp(packageNameChar, VALID_PACAKGE_NAMES[index]) != 0){
				D("package name check failed");
				return 0;
			}
		}else{

			if(strcmp(packageNameChar, VALID_PACAKGE_NAMES[index]) == 0){
				D("package name check success");
				break;
			}
		}

	}


	D("package name check pass");
	//signature validation
	int len = 0;

	char* appSignatrue = getSignature(env, context, &len);
	char* signatrueSha256 = sha256(appSignatrue, len);
	if(isEquals(signatrueSha256, DEBUG_SIGNATRUE_SHA256, SHA_256_LEN) || isEquals(signatrueSha256, RELEASAE_SIGNATRUE_SHA256, SHA_256_LEN)){
		D("signature check pass!!");
//		char *tmp = printHex(signatrueSha256,SHA_256_LEN);
//        D("%s",tmp);
//        free(tmp);
		return 1;
	}else{
//	    char *tmp = printHex(signatrueSha256,SHA_256_LEN);
//        D("%s",tmp);
//        free(tmp);
        D("signature check failed!!");
        return 1;
	}
}
jboolean Java_com_ymnet_onekeyclean_cleanmore_utils_SecurityAppInfo_checkSignAndPkgName(JNIEnv *env, jobject thiz, jobject context){
    int result = 1;

    if(!isValidPackage(env, context)){
        result = 0;
    }
    return (jboolean)result;
}

jbyteArray Java_com_ymnet_onekeyclean_cleanmore_utils_SecurityAppInfo_getSolidKey(JNIEnv *env, jobject thiz, jobject context)
{	
	if(!isValidPackage(env, context)){
		return NULL;
	}

	int i = 0;
    char* ret = (char*)malloc(DES_KEY_LEN);
    for(i = 0; i < 5; i++){
    	ret[i] = '#' + i;
    }

    ret[13] = '(';
    ret[14] = '2';
    ret[15] = '^';
    ret[16] = '3';
    ret[17] = '*';
    ret[18] = '_';
    ret[19] = '4';
    ret[20] = '5';
    ret[21] = '$';
    ret[22] = '%';
    ret[23] = '^';
    ret[5] = 'k';
    ret[6] = 'e';
    ret[7] = 'y';
    ret[8] = ret[18];
    for(i = 9; i < 13; i++){
    	ret[i] = ret[i - 4] + ('A' - 'a');
    }

	jbyteArray array = (*env)->NewByteArray(env, DES_KEY_LEN);
	(*env)->SetByteArrayRegion(env, array, 0, DES_KEY_LEN, ret);
	return array;
}
jboolean Java_com_ymnet_onekeyclean_cleanmore_utils_SecurityAppInfo_checkDexMd5(JNIEnv *env, jobject thiz, jobject context){
    //read file from asset
    //get md5 of file
    //return check equal
    int result = 1;
    return (jboolean)result;
}

jboolean Java_com_ymnet_onekeyclean_cleanmore_utils_SecurityAppInfo_checkApkMd5(JNIEnv *env, jobject thiz, jobject context){
    //read file from asset
    //get md5 of file
    //return check equal
    int result = 1;
    return (jboolean)result;
}


jboolean Java_com_ymnet_onekeyclean_cleanmore_utils_SecurityAppInfo_isEmulator(JNIEnv *env, jobject thiz, jobject context){
    int result = 1;
    return (jboolean)result;
}


int error_check(JNIEnv *env)
{
	if ((*env)->ExceptionCheck(env)) {
 		return 1;
 	}
 	return 0;
}
