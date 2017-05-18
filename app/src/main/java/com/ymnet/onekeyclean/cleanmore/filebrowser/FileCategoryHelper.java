/*
 * Copyright (c) 2010-2011, The MiCode Open Source Community (www.micode.net)
 *
 * This file is part of FileExplorer.
 *
 * FileExplorer is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * FileExplorer is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with SwiFTP.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.ymnet.onekeyclean.cleanmore.filebrowser;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore.Audio;
import android.provider.MediaStore.Files;
import android.provider.MediaStore.Files.FileColumns;
import android.provider.MediaStore.Images;
import android.provider.MediaStore.Video;
import android.util.Log;

import com.ymnet.onekeyclean.R;

import java.io.FilenameFilter;
import java.util.HashMap;
import java.util.Iterator;



@SuppressLint("InlinedApi")
public class FileCategoryHelper {
    public static final int COLUMN_ID = 0;
    
    public static final int COLUMN_PATH = 1;
    
    public static final int COLUMN_SIZE = 2;
    
    public static final int COLUMN_DATE = 3;
    
    private static final String LOG_TAG = "FileCategoryHelper";
    
    public enum FileCategory
    {
        All, Music, Video, Picture, Theme, Doc, Zip, Apk, Custom, Other, OtherPic
    }
    
    private static String APK_EXT = "apk";
    
    private static String THEME_EXT = "mtz";
    
    private static String[] ZIP_EXTS = new String[] { "zip", "rar", "7z", "iso" };
    
    public static final HashMap<FileCategory, FilenameExtFilter> filters = new HashMap<FileCategory, FilenameExtFilter>();
    
    public static final HashMap<FileCategory, Integer> categoryNames = new HashMap<FileCategory, Integer>();
    
    static
    {
        categoryNames.put(FileCategory.All, R.string.category_all);
        categoryNames.put(FileCategory.Music, R.string.category_music);
        categoryNames.put(FileCategory.Video, R.string.category_video);
        categoryNames.put(FileCategory.Picture, R.string.category_picture);
        categoryNames.put(FileCategory.Theme, R.string.category_theme);
        categoryNames.put(FileCategory.Doc, R.string.category_document);
        categoryNames.put(FileCategory.Zip, R.string.category_zip);
        categoryNames.put(FileCategory.Apk, R.string.category_apk);
        categoryNames.put(FileCategory.Other, R.string.category_other);
        categoryNames.put(FileCategory.OtherPic, R.string.category_other_pic);
    }
    
    public static FileCategory[] sCategories = new FileCategory[] { FileCategory.Music, FileCategory.Video, FileCategory.Picture, FileCategory.Theme, FileCategory.Doc, FileCategory.Zip,
            FileCategory.Apk, FileCategory.Other };
    
    private FileCategory mCategory;
    
    private Context mContext;
    
    public FileCategoryHelper(Context context)
    {
        mContext = context;
        
        mCategory = FileCategory.All;
    }
    
    public FileCategory getCurCategory()
    {
        return mCategory;
    }
    
    public void setCurCategory(FileCategory c)
    {
        mCategory = c;
    }
    
    public int getCurCategoryNameResId()
    {
        return categoryNames.get(mCategory);
    }
    
    public void setCustomCategory(String[] exts)
    {
        mCategory = FileCategory.Custom;
        if (filters.containsKey(FileCategory.Custom))
        {
            filters.remove(FileCategory.Custom);
        }
        
        filters.put(FileCategory.Custom, new FilenameExtFilter(exts));
    }
    
    public FilenameFilter getFilter()
    {
        return filters.get(mCategory);
    }
    
    private HashMap<FileCategory, CategoryInfo> mCategoryInfo = new HashMap<FileCategory, CategoryInfo>();
    
    public HashMap<FileCategory, CategoryInfo> getCategoryInfos()
    {
        return mCategoryInfo;
    }

    public class CategoryInfo {}

    private String buildDocSelection()
    {
        StringBuilder selection = new StringBuilder();
        Iterator<String> iter = FileBrowserUtil.sDocMimeTypesSet.iterator();
        while (iter.hasNext())
        {
            selection.append("(" + FileColumns.MIME_TYPE + "=='" + iter.next() + "') OR ");
        }
        return selection.substring(0, selection.lastIndexOf(")") + 1);
    }
    
    private String buildZipSelection()
    {
        StringBuilder selection = new StringBuilder();
        Iterator<String> iter = FileBrowserUtil.sZipFileMimeType.iterator();
        while (iter.hasNext())
        {
            selection.append("(" + FileColumns.MIME_TYPE + "=='" + iter.next() + "') OR ");
        }
        return selection.substring(0, selection.lastIndexOf(")") + 1);
    }
    
    public String buildSelectionByCategory(FileCategory cat)
    {
        String selection = null;
        switch (cat)
        {
            case Theme:
                selection = FileColumns.DATA + " LIKE '%.mtz'";
                break;
            case Doc:
                selection = buildDocSelection();
                break;
            case Zip:
                selection = buildZipSelection();
                break;
            case Apk:
                selection = FileColumns.DATA + " LIKE '%.apk'";
                break;
            case Music:
                selection = null;//MediaStore.Audio.Media.IS_MUSIC + "=1";
                break;
            default:
                selection = null;
        }
        return selection;
    }
    
    @SuppressLint("NewApi")
    private Uri getContentUriByCategory(FileCategory cat)
    {
        Uri uri;
        String volumeName = "external";
        switch (cat)
        {
            case Theme:
            case Doc:
            case Zip:
            case Apk:
                uri = Files.getContentUri(volumeName);
                break;
            case Music:
                uri = Audio.Media.getContentUri(volumeName);
                break;
            case Video:
                uri = Video.Media.getContentUri(volumeName);
                break;
            case Picture:
                uri = Images.Media.getContentUri(volumeName);
                break;
            default:
                uri = null;
        }
        return uri;
    }
    
    public String buildSortOrder(FileSortHelper.SortMethod sort)
    {
        String sortOrder = null;
        switch (sort)
        {
            case name:
                sortOrder = Images.ImageColumns.TITLE + " asc";
                break;
            case size:
                sortOrder = Images.ImageColumns.SIZE + " asc";
                break;
            case date:
                sortOrder = Images.ImageColumns.DATE_MODIFIED + " desc";
                break;
            case type:
                sortOrder = Images.ImageColumns.MIME_TYPE + " asc, " + Images.ImageColumns.TITLE + " asc";
                break;
        }
        return sortOrder;
    }
    
    public Cursor query(FileCategory fc, FileSortHelper.SortMethod sort)
    {
        Uri uri = getContentUriByCategory(fc);
        String selection = buildSelectionByCategory(fc);
        String sortOrder = buildSortOrder(sort);
        
        if (uri == null)
        {
            Log.e(LOG_TAG, "invalid uri, category:" + fc.name());
            return null;
        }
        
        String[] columns = new String[] { FileColumns._ID, FileColumns.DATA, FileColumns.SIZE, FileColumns.DATE_MODIFIED };
        
        return mContext.getContentResolver().query(uri, columns, selection, null, sortOrder);
    }
}
