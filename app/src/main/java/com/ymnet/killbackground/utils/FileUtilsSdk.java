package com.ymnet.killbackground.utils;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.os.Build;
import android.os.Environment;

import com.umeng.analytics.MobclickAgent;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.Enumeration;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class FileUtilsSdk {
	public static final String TAG = "FileUtilsSdk";
	private static String sSDCardStateString;
	public static final String DOWNLOAD_FOLDER_NAME = "Download";
	private static final String COMMON_SDK1_FOLDER_NAME = "SDKCommon";
	public static final String COMMON_SDK1_JSON_DATA_REQUEST_FLAG_FILE_NAME = "commonSDKRequesting";
	public static String sCommonSdk1Dir = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + DOWNLOAD_FOLDER_NAME
			+ File.separator + COMMON_SDK1_FOLDER_NAME + File.separator;

	private static FileUtilsSdk sInstance;
	public static final String APK_FILE_SUFFIX = ".apk";
	public static final String COMMONSDK_TEMP_FILE_SUFFIX = ".commonSDKtemp";
	public static final String WRITE_FILE_TEMP_SUFFIX = ".temp";
    public static String APPINFO_PATH = "META-INF/appinfo";
	private Context mContext;

	public FileUtilsSdk(Context context) {
		mContext = context;
		checkCommonSDK1Dir(context);
	}

	public static void checkCommonSDK1Dir(Context context) {
		String path = File.separator + DOWNLOAD_FOLDER_NAME + File.separator + COMMON_SDK1_FOLDER_NAME + File.separator;
		String commonSdk1DirPath = getSDcardPath(context) + path;
		if (!commonSdk1DirPath.equals(sCommonSdk1Dir)) {
			sCommonSdk1Dir = commonSdk1DirPath;
		}
		File commonSdk1Dir = new File(sCommonSdk1Dir);
		if (!commonSdk1Dir.isDirectory() || !commonSdk1Dir.exists()) {
			creatDir(sCommonSdk1Dir);
		}
	}

	public static String getSDcardPath(Context context) {
		boolean hasSD = Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
		if (hasSD) {
			return Environment.getExternalStorageDirectory().getAbsolutePath();
		} else {
			return context.getFilesDir().getAbsolutePath();
		}
	}


	public static void endCommonSdk1JsonDataRequest(Context context) {
		checkCommonSDK1Dir(context);
		File file = new File(sCommonSdk1Dir + COMMON_SDK1_JSON_DATA_REQUEST_FLAG_FILE_NAME);
		if (file.exists()) {
			deleteFile(file);
		}
	}

	public static File getCommonSdk1DirFile(String fileName, Context context) {
		checkCommonSDK1Dir(context);
		File file = new File(sCommonSdk1Dir + fileName);
		if (!file.exists()) {
			try {
				file.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return file;
	}

	public static String getSDcardDownloadPath(Context context) {
		return getSDcardPath(context) + File.separator + DOWNLOAD_FOLDER_NAME + File.separator;
	}

	public static boolean isPicture(File paramFile) {
		if (paramFile == null) return false;
		return isPicture(paramFile.getAbsolutePath());
	}

	public static boolean isPicture(String paramString) {
		if (paramString != null) {
			File localFile = new File(paramString);
			if ((localFile != null) && (localFile.exists()) && (!localFile.isDirectory())) {
				int i = paramString.lastIndexOf(".");
				if (-1 != i) {
					paramString = paramString.substring(i).toLowerCase();
					if ((paramString != null) && ((paramString.equals(".png")) || (paramString.equals(".jpg")) || (paramString.equals(".jpeg")))) return true;
				}
			}
		}
		return false;
	}

	public static String getCommonSDK1Root(Context context) {
		checkCommonSDK1Dir(context);
		return sCommonSdk1Dir;
	}

	/**
	 * 在SD卡上创建文件
	 *
	 * @param dir
	 *            目录路径
	 * @param fileName
	 * @return
	 * @throws IOException
	 */
	public File createFileInSDKRoot(String dir, String fileName) throws IOException {
		File file = new File(sCommonSdk1Dir + dir + File.separator + fileName);
		file.createNewFile();
		return file;
	}

	public File getFileInSDKRoot(String dir, String fileName) {
		File file = new File(sCommonSdk1Dir + dir + File.separator + fileName);
		return file;
	}

	public File getDataFile(String dir, String fileName) {
		File file = new File(sCommonSdk1Dir + dir + File.separator + fileName);
		return file;
	}

	/**
	 * 在SD卡上创建目录
	 *
	 * @param dir
	 *            目录路径
	 * @return
	 */
	public File creatDirInSDKRoot(String dir) {
		File dirFile = new File(sCommonSdk1Dir + dir + File.separator);
		if (!dirFile.exists()) {
			dirFile.mkdirs();
		}
		return dirFile;
	}

	public static File creatDir(String dir) {
		File dirFile = new File(dir);
		if (!dirFile.exists()) {
			dirFile.mkdirs();
		} else if (dirFile.exists() && !dirFile.isDirectory()) {
			deleteFile(dirFile);
			dirFile.mkdirs();
		}
		return dirFile;
	}

	/***
	 * 获取SD卡的剩余容量,单位是Byte
	 *
	 * @return
	 */
	public long getSDAvailableSize() {
		if (sSDCardStateString.equals(Environment.MEDIA_MOUNTED)) {
			// 取得sdcard文件路径
			File pathFile = Environment.getExternalStorageDirectory();
			android.os.StatFs statfs = new android.os.StatFs(pathFile.getPath());
			// 获取SDCard上每个block的SIZE
			long nBlocSize;
			// 获取可供程序使用的Block的数量
			long nAvailaBlock;
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
				nBlocSize = statfs.getBlockSizeLong();
				nAvailaBlock = statfs.getAvailableBlocksLong();
		}else{
			nBlocSize = statfs.getBlockSize();
			nAvailaBlock = statfs.getAvailableBlocks();
		}
		// 计算 SDCard 剩余大小Byte
		long nSDFreeSize = nAvailaBlock * nBlocSize;
		return nSDFreeSize;
		}
		return 0;
	}

	/**
	 * 将一个字节数组数据写入到SD卡中
	 */
	public boolean write2SDKRoot(String dir, String fileName, byte[] bytes) {
		if (bytes == null) {
			return false;
		}
		OutputStream output = null;
		try {
			// 拥有可读可写权限，并且有足够的容量
			File file = null;
			creatDirInSDKRoot(dir);
			file = createFileInSDKRoot(dir, fileName);
			output = new BufferedOutputStream(new FileOutputStream(file));
			output.write(bytes);
			output.flush();
			return true;
		} catch (IOException e1) {
			MobclickAgent.reportError(mContext,e1.fillInStackTrace());
		} finally {
			try {
				if (output != null) {
					output.close();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return false;
	}

	/***
	 * 从sd卡中读取文件，并且以字节流返回
	 *
	 * @param dir
	 * @param fileName
	 * @return
	 */
	public byte[] readFromSD(String dir, String fileName) {
		File file = getFileInSDKRoot(dir, fileName);
		if (!file.exists()) {
			return null;
		}
		InputStream inputStream = null;
		try {
			inputStream = new BufferedInputStream(new FileInputStream(file));
			byte[] data = new byte[inputStream.available()];
			inputStream.read(data);
			return data;
		} catch (FileNotFoundException e) {
			MobclickAgent.reportError(mContext,e.fillInStackTrace());
		} catch (IOException e) {
			MobclickAgent.reportError(mContext,e.fillInStackTrace());
		} finally {
			try {
				if (inputStream != null) {
					inputStream.close();
				}
			} catch (IOException e) {
				MobclickAgent.reportError(mContext,e.fillInStackTrace());
			}
		}
		return null;
	}

	/**
	 * 将一个InputStream里面的图数据写入到SD卡中 ,从网络上读取片
	 */
	public File write2SDFromInput(String dir, String fileName, InputStream input) {
		File file = null;
		OutputStream output = null;
		try {
			int size = input.available();
			// 拥有可读可写权限，并且有足够的容量
			if (sSDCardStateString.equals(Environment.MEDIA_MOUNTED) && size < getSDAvailableSize()) {
				creatDirInSDKRoot(dir);
				file = createFileInSDKRoot(dir, fileName);
				output = new BufferedOutputStream(new FileOutputStream(file));
				byte buffer[] = new byte[4 * 1024];
				int temp;
				while ((temp = input.read(buffer)) != -1) {
					output.write(buffer, 0, temp);
				}
				output.flush();
			}
		} catch (IOException e1) {
			MobclickAgent.reportError(mContext,e1.fillInStackTrace());
		} finally {
			try {
				if (output != null) {
					output.close();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return file;
	}

	public static int copy(String fromFile, String toFile) {
		// 要复制的文件目录
		File[] currentFiles;
		File root = new File(fromFile);
		// 如同判断SD卡是否存在或者文件是否存在
		// 如果不存在则 return出去
		if (!root.exists()) {
			return -1;
		}
		// 如果存在则获取当前目录下的全部文件 填充数组
		currentFiles = root.listFiles();

		// 目标目录
		File targetDir = new File(toFile);
		// 创建目录
		if (!targetDir.exists()) {
			targetDir.mkdirs();
		}
		// 遍历要复制该目录下的全部文件
		for (int i = 0; i < currentFiles.length; i++) {
			if (currentFiles[i].isDirectory())// 如果当前项为子目录 进行递归
			{
				copy(currentFiles[i].getPath() + "/", toFile + currentFiles[i].getName() + "/");

			} else// 如果当前项为文件则进行文件拷贝
			{
				CopySdcardFile(currentFiles[i].getPath(), toFile + currentFiles[i].getName());
			}
		}
		return 0;
	}

	public static int copyFileToDir(File fromFile, String toFile) {

		// 如同判断SD卡是否存在或者文件是否存在
		// 如果不存在则 return出去
		if (!fromFile.exists()) {
			return -1;
		}

		// 目标目录
		File targetDir = new File(toFile);
		// 创建目录
		if (!targetDir.exists()) {
			targetDir.mkdirs();
		}

		return CopySdcardFile(fromFile.getPath(), toFile + "/" + fromFile.getName());

	}

	// 文件拷贝
	// 要复制的目录下的所有非子目录(文件夹)文件拷贝
	public static int CopySdcardFile(String fromFile, String toFile) {

		try {
			InputStream fosfrom = new FileInputStream(fromFile);
			OutputStream fosto = new FileOutputStream(toFile);
			byte bt[] = new byte[1024];
			int c;
			while ((c = fosfrom.read(bt)) > 0) {
				fosto.write(bt, 0, c);
			}
			fosfrom.close();
			fosto.close();
			return 0;

		} catch (Exception ex) {
			ex.printStackTrace();
			return -1;
		}
	}

	public static void deleteFile(String filePath) {
		File file = new File(filePath);
		deleteFile(file);
	}

	/**
	 * 删除文件夹所有内容
	 * 
	 */
	public static void deleteFile(File file) {
		if (file.exists()) {
			if (file.isFile()) {
				file.delete();
			} else if (file.isDirectory()) {
				File files[] = file.listFiles();
				for (int i = 0; i < files.length; i++) {
					FileUtilsSdk.deleteFile(files[i]);
				}
			}
		} else {
			//
		}
	}

	/**
	 * 判断文件是否存在
	 * 
	 */
	public static boolean isFileExists(String file) {
		File dirFile = new File(file);
		if (dirFile.exists()) {
			return true;
		}
		return false;
	}

	private static final Pattern SAFE_FILENAME_PATTERN = Pattern.compile("[\\w%+,./=_-]+");

	public static boolean copyFile(File srcFile, File destFile) {
		boolean result = false;
		try {
			InputStream in = new FileInputStream(srcFile);
			try {
				result = copyToFile(in, destFile);
			} finally {
				in.close();
			}
		} catch (IOException e) {
			result = false;
		}
		return result;
	}

	public static boolean copyToFile(InputStream inputStream, File destFile) {
		try {
			if (destFile.exists()) {
				destFile.delete();
			}
			OutputStream out = new FileOutputStream(destFile);
			try {
				byte[] buffer = new byte[4096];
				int bytesRead;
				while ((bytesRead = inputStream.read(buffer)) >= 0) {
					out.write(buffer, 0, bytesRead);
				}
			} finally {
				out.close();
			}
			return true;
		} catch (IOException e) {
			return false;
		}
	}

    public static boolean isFilenameSafe(File file) {
        return SAFE_FILENAME_PATTERN.matcher(file.getPath()).matches();
    }

    public static String readTextFile(File file) {
        String fileContent = "";
        try {
            if (file.isFile() && file.exists()) {
                InputStreamReader read = new InputStreamReader(new FileInputStream(file));
                BufferedReader reader = new BufferedReader(read);
                String line;
                while ((line = reader.readLine()) != null) {
                    fileContent += line;
                }
                read.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return fileContent;

    }

    //读取appinfo文件数据
    public static String getFileContent(Context context, String path) {

        String jsonData = "";
        ApplicationInfo appinfo;
        try {
            appinfo = context.getApplicationInfo();
        } catch (Exception e) {
            e.printStackTrace();
            return jsonData;
        }
        String sourceDir = appinfo.sourceDir;
        ZipFile zipfile = null;
        try {
            zipfile = new ZipFile(sourceDir);
            Enumeration<?> entries = zipfile.entries();
            while (entries.hasMoreElements()) {
                ZipEntry entry = ((ZipEntry) entries.nextElement());
                String entryName = entry.getName();

                if (entryName.startsWith(path)) { //xxx 表示要读取的文件名
                    //利用ZipInputStream读取文件
                    long size = entry.getSize();
                    if (size > 0) {
                        BufferedReader br = new BufferedReader(new InputStreamReader(zipfile.getInputStream(entry)));
                        String line;
                        while ((line = br.readLine()) != null) {  //文件内容都在这里输出了，根据你的需要做改变
                            jsonData += line;
                        }
                        br.close();
                    }
                    break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (zipfile != null) {
                try {
                    zipfile.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return jsonData;
    }

    public static String readTextFile(InputStream input, int max, String ellipsis) throws IOException {
        try {
            long size = input.available();
            if (max > 0 || (size > 0 && max == 0)) {
                if (size > 0 && (max == 0 || size < max)) max = (int) size;
                byte[] data = new byte[max + 1];
                int length = input.read(data);
                if (length <= 0) return "";
                if (length <= max) return new String(data, 0, length);
                if (ellipsis == null) return new String(data, 0, max);
                return new String(data, 0, max) + ellipsis;
            } else if (max < 0) { // "tail" mode: keep the last N
                int len;
                boolean rolled = false;
                byte[] last = null, data = null;
                do {
                    if (last != null) rolled = true;
                    byte[] tmp = last;
                    last = data;
                    data = tmp;
                    if (data == null) data = new byte[-max];
                    len = input.read(data);
                } while (len == data.length);
                if (last == null && len <= 0) return "";
                if (last == null) return new String(data, 0, len);
                if (len > 0) {
                    rolled = true;
                    System.arraycopy(last, len, last, 0, last.length - len);
                    System.arraycopy(data, 0, last, last.length - len, len);
                }
                if (ellipsis == null || !rolled) return new String(last);
                return ellipsis + new String(last);
            } else {
                ByteArrayOutputStream contents = new ByteArrayOutputStream();
                int len;
                byte[] data = new byte[1024];
                do {
                    len = input.read(data);
                    if (len > 0) contents.write(data, 0, len);
                } while (len == data.length);
                return contents.toString();
            }
        } finally {
            input.close();
        }

	}

	public static String readTextFile(File file, int max, String ellipsis) throws IOException {
		try {
			InputStream input = new FileInputStream(file);
			return readTextFile(input, max, ellipsis);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public static void writeFileSdcard(String fileName, String message) {
		try {
			String tempFileName = fileName;
			if (!fileName.endsWith(WRITE_FILE_TEMP_SUFFIX)) {
				tempFileName = fileName + WRITE_FILE_TEMP_SUFFIX;
			}

			File tempFile = new File(tempFileName);
			if (tempFile.exists()) {
				return;
			} else {
				tempFile.createNewFile();
			}

			FileOutputStream fout = new FileOutputStream(tempFileName);
			try {
				byte[] bytes = message.getBytes();
				fout.write(bytes);
			} finally {
				if (fout != null) {
					fout.close();
				}
			}

			File file = new File(fileName);
			if (file.exists()) {
				deleteFile(file);
			}

			if (!tempFile.renameTo(file)) {
				deleteFile(tempFile);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static String getFilesDirPath(Context context) {
		String mFileRootPath = null;
		if (mFileRootPath == null) {
			File ExternalFilePath = new File("/mnt/sdcard2/");
			File externalFilesDir = context.getExternalFilesDir(null);
			if (externalFilesDir != null && externalFilesDir.exists()) {
				mFileRootPath = externalFilesDir.getAbsolutePath() + File.separator;
			} else if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
				mFileRootPath = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator;
			} else if (ExternalFilePath.exists() && ExternalFilePath.isDirectory()) {
				mFileRootPath = "/mnt/sdcard2/";
			} else {
				mFileRootPath = context.getFilesDir().getAbsolutePath() + File.separator;
			}
		}
		return mFileRootPath;
	}


}