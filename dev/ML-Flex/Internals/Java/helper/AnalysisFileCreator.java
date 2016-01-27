// THIS SOURCE CODE IS SUPPLIED "AS IS" WITHOUT WARRANTY OF ANY KIND, AND ITS AUTHOR AND THE JOURNAL OF MACHINE LEARNING RESEARCH (JMLR) AND JMLR'S PUBLISHERS AND DISTRIBUTORS, DISCLAIM ANY AND ALL WARRANTIES, INCLUDING BUT NOT LIMITED TO ANY IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE, AND ANY WARRANTIES OR NON INFRINGEMENT. THE USER ASSUMES ALL LIABILITY AND RESPONSIBILITY FOR USE OF THIS SOURCE CODE, AND NEITHER THE AUTHOR NOR JMLR, NOR JMLR'S PUBLISHERS AND DISTRIBUTORS, WILL BE LIABLE FOR DAMAGES OF ANY KIND RESULTING FROM ITS USE. Without lim- iting the generality of the foregoing, neither the author, nor JMLR, nor JMLR's publishers and distributors, warrant that the Source Code will be error-free, will operate without interruption, or will meet the needs of the user.
// 
// --------------------------------------------------------------------------
// 
// Copyright 2016 Stephen Piccolo
// 
// This file is part of ML-Flex.
// 
// ML-Flex is free software: you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// any later version.
// 
// ML-Flex is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.
// 
// You should have received a copy of the GNU General Public License
// along with ML-Flex. If not, see <http://www.gnu.org/licenses/>.

package mlflex.helper;

import mlflex.core.*;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.*;

/** This class is used to transform data from the ML-Flex data format to the format that is required for external software components.
 * @author Stephen Piccolo
 */
public class AnalysisFileCreator
{
    /** These represent file extensions */
    public enum Extension
    {
        ARFF(".arff"),
        ORANGE(".tab"),
        GCT(".gct"),
        CLS(".cls"),
        SURVIVAL("_survival.txt"),
        TAB(".tab"),
        C5NAMES(".names"),
        C5TRAINDATA(".data"),
        C5TESTDATA(".cases");

        private final String _extension;

        Extension(String extension)
        {
            _extension = extension;
        }

        @Override
        public String toString()
        {
            return _extension;
        }
    }

    private String _outputDir;
    private String _fileNamePrefix;
    private DataInstanceCollection _dataInstances;
    private DataInstanceCollection _otherInstances;
    private boolean _includeDependentVariable;
    private ArrayList<String> _features;

    /** Constructor
     *
     * @param outputDirectory Absolute path of directory where files will be saved
     * @param fileNamePrefix Text that will be prepended to each file name that is saved
     * @param dataInstances Collection of data dataInstances
     * @param otherInstances Collection of other data instances that may be necessary for determining all options for a given data point (may be left null)
     * @param includeDependentVariable Whether to include dependent-variable values in the output
     */
    public AnalysisFileCreator(String outputDirectory, String fileNamePrefix, DataInstanceCollection dataInstances, DataInstanceCollection otherInstances, boolean includeDependentVariable, ArrayList<String> features)
    {
        _outputDir = outputDirectory;
        _fileNamePrefix = fileNamePrefix;
        _dataInstances = dataInstances;
        _otherInstances = otherInstances;
        _includeDependentVariable = includeDependentVariable;
        _features = features;
    }

    private String GetDependentVariableValue(String instanceID) throws Exception
    {
        return Singletons.InstanceVault.GetDependentVariableValue(instanceID);
    }

    /** Generates files in the ARFF format.
     * @return This instance
     * @throws Exception
     */
    public AnalysisFileCreator CreateArffFile() throws Exception
    {
        String outFilePath = GetFilePath(Extension.ARFF);
        PrintWriter outFile = new PrintWriter(new BufferedWriter(new FileWriter(outFilePath)));

        outFile.write("@relation thedata\n\n");

        Singletons.Log.Debug("Sorting data point names");
        ArrayList<String> dataPointNames = ListUtilities.SortStringList(ListUtilities.Intersect(_features, _dataInstances.GetDataPointNames()));

        Singletons.Log.Debug("Sorting instance IDs");
        ArrayList<String> instanceIDs = ListUtilities.SortStringList(_dataInstances.GetIDs());

        Singletons.Log.Debug("Appending ARFF attributes for independent variables");
        for (String dataPointName : dataPointNames)
        {
            HashSet<String> uniqueValues = new HashSet<String>(_dataInstances.GetUniqueValues(dataPointName));

            if (_otherInstances != null)
                uniqueValues.addAll(_otherInstances.GetUniqueValues(dataPointName));

            AppendArffAttribute(new ArrayList<String>(uniqueValues), dataPointName, outFile);
        }

        Singletons.Log.Debug("Appending ARFF attributes for dependent variable");
        if (_includeDependentVariable)
            AppendArffAttribute(Singletons.InstanceVault.DependentVariableOptions, Singletons.ProcessorVault.DependentVariableDataProcessor.DataPointName, outFile);

        outFile.write("\n@data");

        Singletons.Log.Debug("Creating ARFF output text object");
        for (String instanceID : instanceIDs)
        {

for (String x : _dataInstances.GetDataPointValues(instanceID, dataPointNames))
	if (x == null)
	{
		Singletons.Log.Info("null value found!!!");
		Singletons.Log.Info(dataPointNames);
		Singletons.Log.Info(_dataInstances.GetDataPointValues(instanceID, dataPointNames));
		System.exit(0);
	}
            outFile.write("\n" + ListUtilities.Join(FormatOutputValues(_dataInstances.GetDataPointValues(instanceID, dataPointNames)), ","));

            if (_includeDependentVariable)
                outFile.write("," + FormatOutputValue(GetDependentVariableValue(instanceID)));
        }
        
        outFile.close();

        return this;
    }

    private void AppendArffAttribute(ArrayList<String> values, String dataPointName, PrintWriter outFile) throws Exception
    {
        outFile.write("@attribute " + dataPointName + " ");

        if (DataTypeUtilities.HasOnlyBinary(values))
            outFile.write("{" + ListUtilities.Join(ListUtilities.SortStringList(values), ",") + "}");
        else
        {
            if (DataTypeUtilities.HasOnlyNumeric(values))
                outFile.write("real");
            else
            {
                FormatOutputValues(values);
                outFile.write("{" + ListUtilities.Join(ListUtilities.SortStringList(values), ",") + "}");
            }
        }
        outFile.write("\n");
    }

    /** This method generates a basic tab-delimited file with variables as rows and instances as columns.
     * @return This instance
     * @throws Exception
     */
    public AnalysisFileCreator CreateTabDelimitedFile() throws Exception
    {
        PrintWriter outFile = new PrintWriter(new BufferedWriter(new FileWriter(GetTabDelimitedFilePath())));
        
        ArrayList<String> dataPoints = ListUtilities.SortStringList(ListUtilities.Intersect(_features, _dataInstances.GetDataPointNames()));
        ArrayList<String> instanceIDs = ListUtilities.SortStringList(_dataInstances.GetIDs());

        ArrayList<String> headerItems = MiscUtilities.UnformatNames(instanceIDs);
        headerItems.add(0, "");
        outFile.write(ListUtilities.Join(headerItems, "\t") + "\n");

        for (String dataPoint : dataPoints)
        {
            ArrayList<String> rowItems = ListUtilities.CreateStringList(dataPoint);

            for (String instanceID : instanceIDs)
                rowItems.add(_dataInstances.GetDataPointValue(instanceID, dataPoint));

            rowItems = ListUtilities.ReplaceAllExactMatches(rowItems, Settings.MISSING_VALUE_STRING, "NA");
            FormatOutputValues(rowItems);

            outFile.write(ListUtilities.Join(rowItems, "\t") + "\n");
        }

        if (_includeDependentVariable)
        {
            ArrayList<String> rowItems = ListUtilities.CreateStringList(Singletons.ProcessorVault.DependentVariableDataProcessor.DataPointName);

            for (String instanceID : instanceIDs)
                rowItems.add(Singletons.InstanceVault.GetDependentVariableValue(instanceID));

            outFile.write(ListUtilities.Join(rowItems, "\t") + "\n");
        }

        outFile.close();
        
        return this;
    }

    /** This method generates a transposed tab-delimited file with variables as columns and instances as rows.
     * @param includeInstanceIDs Whether to include the ID of each instance in the file
     * @return This instance
     * @throws Exception
     */
    public AnalysisFileCreator CreateTransposedTabDelimitedFile(boolean includeInstanceIDs) throws Exception
    {
        PrintWriter outFile = new PrintWriter(new BufferedWriter(new FileWriter(GetTabDelimitedFilePath())));
        
        ArrayList<String> dataPointNames = ListUtilities.SortStringList(ListUtilities.Intersect(_features, _dataInstances.GetDataPointNames()));

        ArrayList<String> headerDataPoints = MiscUtilities.UnformatNames(new ArrayList<String>(dataPointNames));

        if (includeInstanceIDs)
            headerDataPoints.add(0, "ID");

        if (_includeDependentVariable)
            headerDataPoints.add(Singletons.ProcessorVault.DependentVariableDataProcessor.DataPointName);

        outFile.write(ListUtilities.Join(headerDataPoints, "\t") + "\n");

        for (String instanceID : _dataInstances)
        {
            ArrayList<String> values = _dataInstances.GetDataPointValues(instanceID, dataPointNames);

            if (includeInstanceIDs)
                values.add(0, instanceID);

            if (_includeDependentVariable)
                values.add(GetDependentVariableValue(instanceID));

            values = ListUtilities.ReplaceAllExactMatches(values, Settings.MISSING_VALUE_STRING, "NA");

            FormatOutputValues(values);

            outFile.write(ListUtilities.Join(values, "\t") + "\n");
        }
        
        outFile.close();

        return this;
    }

    /** This method generates a text file in the format required by the Orange machine-learning framework.
     * @return This instance
     * @throws Exception
     */
    public AnalysisFileCreator CreateOrangeFile() throws Exception
    {
        DataInstanceCollection instances = _dataInstances;

        String outFilePath = GetFilePath(Extension.ORANGE);
        ArrayList<String> dataPointNames = ListUtilities.SortStringList(ListUtilities.Intersect(_features, _dataInstances.GetDataPointNames()));

        String header = ListUtilities.Join(dataPointNames, "\t");
        header += _includeDependentVariable ? "\t" + Singletons.ProcessorVault.DependentVariableDataProcessor.DataPointName : "";
        header += "\n" + ListUtilities.Join(GetOrangeAttributeHeader(instances, dataPointNames), "\t");
        header += _includeDependentVariable ? "\td" : "";
        header += "\n" + ListUtilities.Join(ListUtilities.CreateStringList("", dataPointNames.size() + 1), "\t");
        header += _includeDependentVariable ? "class" : "";
        header += "\n";

        FileUtilities.WriteTextToFile(outFilePath, header);

        for (String instanceID : instances)
        {
            String line = ListUtilities.Join(FormatOutputValues(_dataInstances.GetDataPointValues(instanceID, dataPointNames)), "\t");

            if (_includeDependentVariable)
                line += "\t" + FormatOutputValue(GetDependentVariableValue(instanceID));

            FileUtilities.AppendTextToFile(outFilePath, line + "\n");
        }

        return this;
    }

    private ArrayList<String> GetOrangeAttributeHeader(DataInstanceCollection instances, ArrayList<String> dataPointNames)
    {
        ArrayList<String> results = new ArrayList<String>();

        for (String dataPointName : dataPointNames)
        {
            ArrayList<String> uniqueValues = instances.GetUniqueValues(dataPointName);
            results.add((DataTypeUtilities.HasOnlyNumeric(uniqueValues) && !DataTypeUtilities.HasOnlyBinary(uniqueValues)) ? "c" : "d");
        }

        return results;
    }

    /** This method creates text files in the format required by the C5.0 software. Specifically, it generates .names files.
     * @return This instance
     * @throws Exception
     */
    public AnalysisFileCreator CreateC5NamesFile() throws Exception
    {
        StringBuilder output = new StringBuilder();
        output.append(ListUtilities.Join(Singletons.InstanceVault.DependentVariableOptions, ", ") + ".\n\n");
        
        ArrayList<String> dataPointNames = ListUtilities.SortStringList(ListUtilities.Intersect(_features, _dataInstances.GetDataPointNames()));

        for (String dataPointName : dataPointNames)
        {
            output.append(dataPointName + ":\t");
            ArrayList<String> uniqueDataValues = _dataInstances.GetUniqueValues(dataPointName);

            if (DataTypeUtilities.HasOnlyNumeric(uniqueDataValues) && !DataTypeUtilities.HasOnlyBinary(uniqueDataValues))
                output.append("continuous");
            else
            {
                AnalysisFileCreator.FormatOutputValues(uniqueDataValues);
                output.append(ListUtilities.Join(uniqueDataValues, ", "));
            }

            output.append(".\n");
        }

        FileUtilities.WriteTextToFile(GetC5NamesFilePath(), output.toString());

        return this;
    }

    /** This method creates text files in the format required by the C5.0 software. Specifically, it generates a .data file to be used for training a model.
     * @return This instance
     * @throws Exception
     */
    public AnalysisFileCreator CreateC5TrainDataFile() throws Exception
    {
        return CreateC5DataFile(GetC5TrainDataFilePath(), false);
    }

    /** This method creates text files in the format required by the C5.0 software. Specifically, it generates a .data file to be used for Action.
     * @return This instance
     * @throws Exception
     */
    public AnalysisFileCreator CreateC5TestDataFile() throws Exception
    {
        return CreateC5DataFile(GetC5TestDataFilePath(), true);
    }

    private AnalysisFileCreator CreateC5DataFile(String filePath, boolean areTestInstances) throws Exception
    {
        StringBuilder output = new StringBuilder();

        for (String instanceID : _dataInstances)
        {
            ArrayList<String> values = _dataInstances.GetDataPointValues(instanceID, _dataInstances.GetDataPointNames());
            values.add(areTestInstances ? "?" : GetDependentVariableValue(instanceID));
            output.append(ListUtilities.Join(values, ",") + "\n");
        }

        FileUtilities.WriteTextToFile(filePath, output.toString());

        return this;
    }

    /** Deletes an analysis file that has already been created.
     * @param extension File extension
     * @throws Exception
     */
    public void DeleteFile(Extension extension) throws Exception
    {
        FileUtilities.DeleteFile(GetFilePath(extension));
    }

    /** Deletes an ARFF file that has already been created.
     * @throws Exception
     */
    public void DeleteArffFile() throws Exception
    {
        DeleteFile(Extension.ARFF);
    }

    /** Deletes an Orange file that has already been created.
     * @throws Exception
     */
    public void DeleteOrangeFile() throws Exception
    {
        DeleteFile(Extension.ORANGE);
    }

//    public void DeleteMlpyFile() throws Exception
//    {
//        DeleteFile(Extension.MLPY);
//    }

    /** Deletes a tab-delimited file that has already been created.
     *
     * @throws Exception
     */
    public void DeleteTabDelimitedFile() throws Exception
    {
        DeleteFile(Extension.TAB);
    }

    /** Deletes a tab-delimited file that has already been created.
     *
     * @throws Exception
     */
    public void DeleteTransposedTabDelimitedFile() throws Exception
    {
        DeleteFile(Extension.TAB);
    }

    private String GetFilePath(Extension extension)
    {
        return _outputDir + _fileNamePrefix + extension.toString();
    }

    public String GetArffFilePath()
    {
        return GetFilePath(Extension.ARFF);
    }

    public String GetTabDelimitedFilePath()
    {
        return GetFilePath(Extension.TAB);
    }

    public String GetTransposedTabDelimitedFilePath()
    {
        return GetFilePath(Extension.TAB);
    }

    public String GetC5NamesFilePath()
    {
        return GetFilePath(Extension.C5NAMES);
    }

    public String GetC5TrainDataFilePath()
    {
        return GetFilePath(Extension.C5TRAINDATA);
    }

    public String GetC5TestDataFilePath()
    {
        return GetFilePath(Extension.C5TESTDATA);
    }

    public String GetOrangeFilePath()
    {
        return GetFilePath(Extension.ORANGE);
    }

//    public String GetMlpyFilePath()
//    {
//        return GetFilePath(Extension.MLPY);
//    }

    public static ArrayList<String> FormatOutputValues(ArrayList<String> values)
    {
        for (int i = 0; i < values.size(); i++)
            values.set(i, FormatOutputValue(values.get(i)));

        return values;
    }

    public static String FormatOutputValue(String value)
    {
        return value.replace(" ", "_");
    }
}
