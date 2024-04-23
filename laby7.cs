using System;
using System.IO;
using System.Linq;
using System.Collections.Generic;
using System.Runtime.Serialization.Formatters.Binary;

public static class FileSystemInfoExtensions
{
    public static string GetDosAttributes(this FileSystemInfo fileSystemInfo)
    {
        string dosAttributes = "";

        if ((fileSystemInfo.Attributes & FileAttributes.ReadOnly) == FileAttributes.ReadOnly)
        {
            dosAttributes += "R";
        }
        else
        {
            dosAttributes += "-";
        }
        if ((fileSystemInfo.Attributes & FileAttributes.Hidden) == FileAttributes.Hidden)
        {
            dosAttributes += "H";
        }
        else
        {
            dosAttributes += "-";
        }
        if ((fileSystemInfo.Attributes & FileAttributes.System) == FileAttributes.System)
        {
            dosAttributes += "S";
        }
        else
        {
            dosAttributes += "-";
        }
        if ((fileSystemInfo.Attributes & FileAttributes.Archive) == FileAttributes.Archive)
        {
            dosAttributes += "A";
        }
        else
        {
            dosAttributes += "-";
        }

        return dosAttributes;
    }
}


public static class DirectoryInfoExtensions
{
    public static void DisplayDirectoryContents(this DirectoryInfo directory, string indent = "")
    {
        Console.WriteLine($"{indent}{directory.Name} ({directory.GetFiles().Length + directory.GetDirectories().Length})");

        FileInfo[] files = directory.GetFiles();

        foreach (FileInfo file in files)
        {
            Console.WriteLine($"{indent}  {file.Name} {file.Length} bajtow {file.GetDosAttributes()}");
        }

        DirectoryInfo[] subdirectories = directory.GetDirectories();

        foreach (DirectoryInfo subdirectory in subdirectories)
        {
            subdirectory.DisplayDirectoryContents(indent + "  ");
        }
    }

    public static DateTime FindOldestDate(this DirectoryInfo directory)
    {
        DateTime oldestDate = directory.LastWriteTime;

        FileInfo[] files = directory.GetFiles();
        if (files.Any())
        {
            DateTime oldestFileDate = files.Min(f => f.LastWriteTime);
            if (oldestFileDate < oldestDate)
            {
                oldestDate = oldestFileDate;
            }
        }

        DirectoryInfo[] subdirectories = directory.GetDirectories();
        foreach (var subdirectory in subdirectories)
        {
            DateTime subdirectoryOldestDate = subdirectory.FindOldestDate();
            if (subdirectoryOldestDate < oldestDate)
            {
                oldestDate = subdirectoryOldestDate;
            }
        }

        return oldestDate;
    }
}

class Program
{
    static void Main(string[] args)
    {
        if (args.Length == 0)
        {
            Console.WriteLine("Nie podano ścieżki do katalogu.");
            return;
        }

        string directoryPath = args[0];

        if (!Directory.Exists(directoryPath))
        {
            Console.WriteLine("Podana ścieżka nie istnieje.");
            return;
        }

        DirectoryInfo directory = new DirectoryInfo(directoryPath);
        directory.DisplayDirectoryContents();


        DateTime oldestDate = directory.FindOldestDate();

        Console.WriteLine("");
        Console.WriteLine($"Najstarszy plik {oldestDate}");


        SortedDictionary<string, long> directoryContents = LoadDirectoryContents(directoryPath);


        string foo = "/home/kamil/polibuda/platformy/foo";
        SerializeCollection(directoryContents, foo);
        SortedDictionary<string, long> deserializedCollection = DeserializeCollection(foo);

        Console.WriteLine("Zawartość katalogu (uporządkowana):");
        foreach (var item in deserializedCollection)
        {
            Console.WriteLine($"{item.Key}: {item.Value} bytes");
        }
    }

    static SortedDictionary<string, long> LoadDirectoryContents(string directoryPath)
    {
        var comparer = new StringLengthAlphabeticalComparer();
        SortedDictionary<string, long> directoryContents = new SortedDictionary<string, long>(comparer);
        string[] files = Directory.GetFiles(directoryPath);
        foreach (string file in files)
        {
            FileInfo fileInfo = new FileInfo(file);
            directoryContents.Add(fileInfo.Name, fileInfo.Length);
        }
        string[] subdirectories = Directory.GetDirectories(directoryPath);
        foreach (string subdirectory in subdirectories)
        {
            DirectoryInfo directoryInfo = new DirectoryInfo(subdirectory);
            directoryContents.Add(directoryInfo.Name, GetDirectorySize(subdirectory));
        }

        return directoryContents;
    }

    static long GetDirectorySize(string directoryPath)
    {
        long size = 0;
        string[] files = Directory.GetFiles(directoryPath);
        foreach (string file in files)
        {
            FileInfo fileInfo = new FileInfo(file);
            size += fileInfo.Length;
        }
        return size;
    }

    static void SerializeCollection(SortedDictionary<string, long> collection, string filePath)
    {
        try
        {
            BinaryFormatter formatter = new BinaryFormatter();
            using (FileStream stream = new FileStream(filePath, FileMode.Create))
            {
                formatter.Serialize(stream, collection);
            }

            Console.WriteLine("Kolekcja została pomyślnie zserializowana.");
        }
        catch (Exception ex)
        {
            Console.WriteLine($"Wystąpił błąd podczas serializacji kolekcji: {ex.Message}");
        }
    }

    static SortedDictionary<string, long> DeserializeCollection(string filePath)
    {
        try
        {
            BinaryFormatter formatter = new BinaryFormatter();
            using (FileStream stream = new FileStream(filePath, FileMode.Open))
            {
                return (SortedDictionary<string, long>)formatter.Deserialize(stream);
            }
        }
        catch (Exception ex)
        {
            Console.WriteLine($"Wystąpił błąd podczas deserializacji kolekcji: {ex.Message}");
            return null;
        }
    }
}

[Serializable]
class StringLengthAlphabeticalComparer : IComparer<string>
{
    public int Compare(string x, string y)
    {
        int lengthComparison = x.Length.CompareTo(y.Length);
        if (lengthComparison != 0)
        {
            return lengthComparison;
        }
        else
        {
            return string.Compare(x, y);
        }
    }
}
