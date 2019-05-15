package com.recruit.pdfreader.utils;

import android.text.TextUtils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.nio.channels.FileLock;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FileUtil {

	/**
	 * 创建一个新的文件或文件夹
	 * 
	 * @param path
	 *            绝对路径
	 * @return File
	 * @throws IOException
	 */
	public static File newFile(String path) throws IOException {
		File file = new File(path);
		if (file.exists()) {
			return file;
		}
		try {
			// 新建文件及其父目录
			File dir = file.getParentFile();
			if (!dir.exists()) {
				dir.mkdirs();
			}
			if (file.getName().contains(".")) {
				file.createNewFile();
			} else {
				file.mkdirs();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		if (file.exists()) {
			return file;
		} else {
			return null;
		}
	}

	/**
	 * 获取文件/文件夹的大小(bytes)
	 *
	 * @param path
	 *            文件路径
	 * @return 文件/文件夹的大小(bytes)
	 */
	public static long getSize(String path) {
		try {
			File file = new File(path);
			if (file.isDirectory()) {
				File[] files = file.listFiles();
				long size = 0L;
				for (File f : files) {
					size += getSize(f.getAbsolutePath());
				}
				return size;
			} else {
				return file.length();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return 0L;
	}

	/**
	 * 判断文件是否存在
	 *
	 * @param path
	 *            文件路径
	 * @return 文件是否存在
	 */
	public static boolean exists(String path) {
	    if (path == null || path.length() == 0) {
	        return false;
	    }
		try {
			File file = new File(path);
			return file.exists();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * 根据路径删除指定的目录或文件，无论存在与否
	 *
	 * @param sPath
	 *            要删除的目录或文件
	 * @return 删除成功返回 true，否则返回 false。
	 */
	public static boolean delete(String sPath) {
		boolean flag = false;
		File file = new File(sPath);
		// 判断目录或文件是否存在
		if (!file.exists()) { // 不存在返回 false
			return flag;
		} else {
			// 判断是否为文件
			if (file.isFile()) { // 为文件时调用删除文件方法
				return deleteFile(sPath);
			} else { // 为目录时调用删除目录方法
				return deleteDirectory(sPath);
			}
		}
	}

	/**
	 * 删除单个文件
	 *
	 * @param sPath
	 *            被删除文件的文件名
	 * @return 单个文件删除成功返回true，否则返回false
	 */
	private static boolean deleteFile(String sPath) {
		boolean flag = false;
		File file = new File(sPath);
		// 路径为文件且不为空则进行删除
		if (file.isFile() && file.exists()) {
			file.delete();
			flag = true;
		}
		return flag;
	}

	/**
	 * 删除目录（文件夹）以及目录下的文件
	 *
	 * @param sPath
	 *            被删除目录的文件路径
	 * @return 目录删除成功返回true，否则返回false
	 */
	private static boolean deleteDirectory(String sPath) {
		// 如果sPath不以文件分隔符结尾，自动添加文件分隔符
		if (!sPath.endsWith(File.separator)) {
			sPath = sPath + File.separator;
		}
		File dirFile = new File(sPath);
		// 如果dir对应的文件不存在，或者不是一个目录，则退出
		if (!dirFile.exists() || !dirFile.isDirectory()) {
			return false;
		}
		boolean flag = true;
		// 删除文件夹下的所有文件(包括子目录)
		File[] files = dirFile.listFiles();
		for (int i = 0; i < files.length; i++) {
			// 删除子文件
			if (files[i].isFile()) {
				flag = deleteFile(files[i].getAbsolutePath());
				if (!flag)
					break;
			} // 删除子目录
			else {
				flag = deleteDirectory(files[i].getAbsolutePath());
				if (!flag)
					break;
			}
		}
		if (!flag)
			return false;
		// 删除当前目录
		if (dirFile.delete()) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 复制文件/文件夹
	 *
	 * @param oldPath
	 *            原文件路径
	 * @param newPath
	 *            目标文件路径
	 * @return 是否成功
	 */
	public static boolean copy(String oldPath, String newPath) {
		if (exists(oldPath)) {
		    if (oldPath.equals(newPath)) {
		        return true;
		    }
			File file = new File(oldPath);
			// 判断目录或文件是否存在
			// 判断是否为文件
			try {
			    newFile(newPath);
				if (file.isFile()) { // 为文件时调用删除文件方法
					copyFile(file, new File(newPath));
				} else { // 为目录时调用删除目录方法
					copyDirectiory(oldPath, newPath);
				}
				return exists(newPath);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return false;
	}

	// 复制文件
	private static void copyFile(File sourceFile, File targetFile)
			throws IOException {
		BufferedInputStream inBuff = null;
		BufferedOutputStream outBuff = null;
		try {
			// 新建文件输入流并对它进行缓冲
			inBuff = new BufferedInputStream(new FileInputStream(sourceFile));

			// 新建文件输出流并对它进行缓冲
			outBuff = new BufferedOutputStream(new FileOutputStream(targetFile));

			// 缓冲数组
			byte[] b = new byte[1024 * 5];
			int len;
			while ((len = inBuff.read(b)) != -1) {
				outBuff.write(b, 0, len);
			}
			// 刷新此缓冲的输出流
			outBuff.flush();
		} finally {
			// 关闭流
			if (inBuff != null)
				inBuff.close();
			if (outBuff != null)
				outBuff.close();
		}
	}

	// 复制文件夹
	private static void copyDirectiory(String sourceDir, String targetDir)
			throws IOException {
		// 新建目标目录
		(new File(targetDir)).mkdirs();
		// 获取源文件夹当前下的文件或目录
		File[] file = (new File(sourceDir)).listFiles();
		for (int i = 0; i < file.length; i++) {
			if (file[i].isFile()) {
				// 源文件
				File sourceFile = file[i];
				// 目标文件
				File targetFile = new File(
						new File(targetDir).getAbsolutePath() + File.separator
								+ file[i].getName());
				copyFile(sourceFile, targetFile);
			}
			if (file[i].isDirectory()) {
				// 准备复制的源文件夹
				String dir1 = sourceDir + "/" + file[i].getName();
				// 准备复制的目标文件夹
				String dir2 = targetDir + "/" + file[i].getName();
				copyDirectiory(dir1, dir2);
			}
		}
	}

	/**
	 * 移动文件/文件夹
	 *
	 * @param oldPath
	 *            原文件路径
	 * @param newPath
	 *            目标文件路径
	 * @return 是否成功
	 */
	public static boolean move(String oldPath, String newPath) {
		if (copy(oldPath, newPath)) {
			return delete(oldPath);
		}
		return false;
	}

	/**
	 * 过滤文件夹里的文件
	 *
	 * @param subDirectory
	 *            是否包括子文件夹
	 * @param path
	 *            文件夹路径
	 * @param name
	 *            匹配名称,null时不匹配名称
	 * @param suffix
	 *            匹配后缀(类型),null时不匹配后缀(类型)
	 * @return 当时name和suffix都为null时,返回null
	 */
	public static List<File> getFiles(String path, boolean subDirectory,
			String name, String suffix) {
		if (name == null && suffix == null) {
			return null;
		}
		List<File> fileList = new ArrayList<File>();
		File filePath = new File(path);
		if (filePath.exists() && filePath.isDirectory()) {
			File[] files = filePath.listFiles();
			for (File file : files) {
				System.out.println("--------"+file.getAbsolutePath());
				if (file.isDirectory() && subDirectory) {
					fileList.addAll(getFiles(file.getAbsolutePath(), name,
							suffix));
				} else {
					boolean nameFlag = true;
					boolean typeFlag = true;
					if (name != null && !matchName(file, name, false)) {
						nameFlag = false;
					}

					if (suffix != null && !matchSuffix(file, suffix)) {
						typeFlag = false;
					}

					if (nameFlag && typeFlag) {
						fileList.add(file);
					}
				}
			}
		}
		return fileList;
	}

	/**
	 * 过滤文件夹里的文件
	 *
	 * @param subDirectory
	 *            是否包括子文件夹
	 * @param path
	 *            文件夹路径
	 * @param name
	 *            匹配名称,null时不匹配名称
	 * @param isEqual
	 *            是否完全匹配名称
	 * @param suffix
	 *            匹配后缀(类型),null时不匹配后缀(类型)
	 * @return 当时name和suffix都为null时,返回null
	 */
	public static List<File> getFiles(String path, boolean subDirectory,
			String name, boolean isEqual, String suffix) {
		if (name == null && suffix == null) {
			return null;
		}
		List<File> fileList = new ArrayList<File>();
		File filePath = new File(path);
		if (filePath.exists() && filePath.isDirectory()) {
			File[] files = filePath.listFiles();
			for (File file : files) {
				if (file.isDirectory() && subDirectory) {
					fileList.addAll(getFiles(file.getAbsolutePath(),
							subDirectory, name, isEqual, suffix));
				} else {
					boolean nameFlag = true;
					boolean typeFlag = true;
					if (name != null) {
						if (isEqual && !matchName(file, name, false)) {
							nameFlag = false;
						} else if (!isEqual
								&& !getNameNoSuffix(file).contains(name)) {
							nameFlag = false;
						}
					}

					if (suffix != null && !matchSuffix(file, suffix)) {
						typeFlag = false;
					}

					if (nameFlag && typeFlag) {
						fileList.add(file);
					}
				}
			}
		}
		return fileList;
	}

	/**
	 * 过滤文件夹里的文件
	 *
	 * @param path
	 *            文件夹路径
	 * @param filter
	 *            过滤器
	 * @return
	 */
	public static List<File> getFiles(String path, FileFilter filter) {
		List<File> fileList = new ArrayList<File>();
		File filePath = new File(path);
		if (filePath.exists() && filePath.isDirectory()) {
			File[] files = filePath.listFiles(filter);
			fileList.addAll(Arrays.asList(files));
		}
		return fileList;
	}

	/**
	 * 过滤文件夹里的文件,包括子文件夹
	 *
	 * @param path
	 *            文件夹路径
	 * @param name
	 *            匹配名称,null时不匹配名称
	 * @param suffix
	 *            匹配后缀(类型),null时不匹配后缀(类型)
	 * @return 当时name和suffix都为null时,返回null
	 */
	public static List<File> getFiles(String path, String name, String suffix) {
		return getFiles(path, true, name, suffix);
	}

	/**
	 * 获取文件夹/文件(不含后缀)的名称
	 *
	 * @param file
	 *            文件夹/文件
	 * @return 当文件夹/文件不存在时,返回null
	 */
	public static String getNameNoSuffix(File file) {
		if (exists(file.getAbsolutePath())) {
			String path = file.getName();
			int index = path.lastIndexOf(".");
			String name = path.substring(0, index == -1 ? file.getName()
					.length() : path.lastIndexOf("."));
			return name;
		}
		return null;
	}

	/**
	 * 获取文件后缀名(不含".")
	 *
	 * @param file
	 *            文件
	 * @return 当文件没有后缀名时, 返回null
	 */
	public static String getSuffix(File file) {
	    String path = file.getName();
        if (TextUtils.isEmpty(path)) {
            return null;
        }
        int index = path.lastIndexOf(".");
        if (index != -1) {
            String suffix = path.substring(index + 1);
            return suffix;
        }
        return null;
	}

	/**
	 * 匹配文件夹/文件(不含后缀)的名称
	 *
	 * @param file
	 *            文件夹/文件
	 * @param name
	 *            匹配
	 * @param ignoreCase
	 *            是否忽略大小写
	 * @return
	 */
	public static boolean matchName(File file, String name, boolean ignoreCase) {
		if (ignoreCase) {
			return name.equalsIgnoreCase(getNameNoSuffix(file));
		} else {
			return name.equals(getNameNoSuffix(file));
		}
	}

	/**
	 * 匹配文件类型
	 *
	 * @param file
	 *            文件
	 * @param suffix
	 *            后缀/类型
	 * @return
	 */
	public static boolean matchSuffix(File file, String suffix) {
		return suffix.equalsIgnoreCase(getSuffix(file));
	}

	/**
     * 文件转化byte[]操作
     * @param fileName 文件路径
     * @return 文件的byte[]格式
     */
    public static byte[] fileToByte(String fileName) {
        try {
            return fileToByte(new File(fileName));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 文件转化byte[]操作
     *
     * @param file 需要转化为byte[]的文件
     * @return 文件的byte[]格式
     * @throws IOException IO流异常
     */
    public static byte[] fileToByte(File file) throws IOException {
        InputStream in = new FileInputStream(file);
        return inputStreamToByte(in);
    }

    /**
     *
     * @param in
     * @return 文件的byte[]格式
     * @throws IOException IO流异常
     */
    public static byte[] inputStreamToByte(InputStream in) throws IOException {
		try {
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            byte[] barr = new byte[1024];
            while (true) {
                int r = in.read(barr);
                if (r <= 0) {
                    break;
                }
                buffer.write(barr, 0, r);
            }
            return buffer.toByteArray();
        } finally {
            closeStream(in);
        }
	}
    
    /**
     * 将文件的byte[]格式转化成一个文件
     * 
     * @param b 文件的byte[]格式
     * @param fileName 文件名称
     * @return 转化后的文件
     */
    public static File byteToFile(byte[] b, String fileName) {
        BufferedOutputStream bos = null;
        File file = null;
        // 增加文件锁处理
        FileLock fileLock = null;
        try {
            file = new File(fileName);
            if (!file.exists()) {
                File parent = file.getParentFile();
                // 此处增加判断parent != null && !parent.exists()
                if (parent != null && !parent.exists() && !parent.mkdirs()) {
                    // 创建不成功的话，直接返回null
                    return null;
                }
            }
            FileOutputStream fos = new FileOutputStream(file);
            // 获取文件锁
            fileLock = fos.getChannel().tryLock();
            if (fileLock != null) {
                bos = new BufferedOutputStream(fos);
                bos.write(b);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fileLock != null) {
                try {
                    fileLock.release();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            // ***END***  [ST2-图片读取管理] 王玉丰 2012-8-6 modify
            closeStream(bos);
        }
        return file;
    }
    
    /**
     * 专门用来关闭可关闭的流
     * 
     * @param beCloseStream 需要关闭的流
     * @return 已经为空或者关闭成功返回true，否则返回false
     */
    public static boolean closeStream(java.io.Closeable beCloseStream) {
        if (beCloseStream != null) {
            try {
                beCloseStream.close();
                return true;
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
        }
        return true;
    }


	public static String fileToString(String path) {
		File file = new File(path);
		FileReader reader = null;
		try {
			reader = new FileReader(file);
			int fileLen = (int)file.length();
			char[] chars = new char[fileLen];
			reader.read(chars);
			String txt = String.valueOf(chars);
			return txt;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static void main(String[] args) {
//		File file = new File("e:/LDRYSystem.db");
//		System.out.println(getFiles("E:/test", true, "a", false, null));
		// System.out.println(getFiles("E:/test", "aaa", null));

		String path = "/Users/hendy/Development/Projects/Recruit.com.hk/JsonTxt/Recruit 2016/LandJson.txt";
		String txt = FileUtil.fileToString(path);
		System.out.print(txt);


	}

}
