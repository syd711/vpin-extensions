'**********************************************************
'* Sample VB.net Plugin implementation for the B2S.Server *
'**********************************************************

Imports System.Windows.Forms
Imports System.IO
Imports System.Net

''' <summary>
''' Main class of the B2S.Server plugin.<br/>
''' This class must implement the IDirectPlugin interface provided by the B2SServerPluginInterface.dll.<br/>
''' If a plugin provides a frontend the IDirectPluginFrontend interface has to be implemented as well.<br/>
''' For plugins wanting to receive updates on important PinMame actions the IDirectPluginPinMame interface has to be implemented.<br/>
''' <br/>
''' In addition to the implementation of the necessary interfaces, the class has to be exported for the use with MEF using the following attribute  [Export(typeof(B2S.IDirectPlugin))] (for VB.net &lt;Export(GetType(B2S.IDirectPlugin))&gt; would be the same).
''' \remark Remember to change the name of the class to something meaningful for your plugin project when reusing this code.
''' </summary>

<Export(GetType(IDirectPlugin))>
Public Class B2SCurlPluginVBNet
    Implements IDirectPlugin, IDirectPluginFrontend, IDirectPluginPinMame


#Region "IDirectPlugin Members"

    ''' <summary>
    ''' Gets the name of the plugin.<br/>
    ''' When implmenting this property it is recommended to add the version of the plugin to the name as well.<br/> 
    ''' The IDirectPlugin interface requires the implementation of the property.<br/>
    ''' \remark If the code of this implementation of the property is reused, be sure to set the versioning information in AssemblyInfo.cs to something like [assembly: AssemblyVersion("1.0.*")]. Otherwise the BuildDate will not be correct.<br/>
    ''' </summary>
    ''' <value>
    ''' The name of the IDirectPlugin.
    ''' </value>
    Public ReadOnly Property Name As String Implements IDirectPlugin.Name
        Get
            'Get the version of the assembly
            Dim V As Version = System.Reflection.Assembly.GetExecutingAssembly().GetName().Version
            'Calculate the BuildDate based in the build and revsion number of the project.
            Dim BuildDate As DateTime = New DateTime(2000, 1, 1).AddDays(V.Build).AddSeconds(V.Revision * 2)
            'Format and return the name string.
            Return String.Format("B2S Curl Plugin VB.net (V: {0} as of {1})", V.ToString(), BuildDate.ToString("yyyy.MM.dd hh:mm"))
        End Get

    End Property

    ''' <summary>
    ''' Initializes the Plugin.<br/>
    ''' This is the first method, which is called after the plugin has been instanciated by the B2S.Server.<br/>
    ''' The IDirectPlugin interface requires the implementation of this method.<br/>
    ''' </summary>
    ''' <param name="TableFilename">The table filename.</param>
    ''' <param name="RomName">Name of the rom.</param>
    Public Sub PluginInit(TableFilename As String, Optional RomName As String = "") Implements IDirectPlugin.PluginInit

    End Sub


    ''' <summary>
    ''' Finishes the plugin.<br />
    ''' This is the last method called, before a plugin is discared. This method is also called, after a undhandled exception has occured in a plugin.<br/>
    ''' PluginFinish must do all nessecary clean up work for the plugin (e.g. release resources).<br/>
    ''' The IDirectPlugin interface requires the implementation of this method.<br/>
    ''' </summary>
    Public Sub PluginFinish() Implements IDirectPlugin.PluginFinish

    End Sub

    Public Property strFile As String = "C:\vPinball\vpin.txt"

    Public Property fileExists As Boolean = File.Exists(strFile)
    Public Property sw As New StreamWriter(File.Open(strFile, FileMode.OpenOrCreate))
    Public Property webClient As WebClient = New WebClient()

    ''' <summary>
    ''' This method is called, when new data from PinMame becomes available.<br/>
    ''' The IDirectPlugin interface requires the implementation of this method.<br/>
    ''' \remark The special care when implementing to keep this method very fast! Slow implementations will slow down Visual Pinball, Pinmame, the B2S.Server as well as all other plugins. 
    ''' \remark The best solution is to put the data in a queue and process the data in a separate thread.    
    ''' </summary>
    ''' <param name="TableElementTypeChar">Char representing the table element type (S=Solenoid, W=Switch, L=Lamp, M=Mech, G=GI, E=EMTable, ?=unknown table element type).</param>
    ''' <param name="Number">The number of the table element.</param>
    ''' <param name="Value">The value of the table element.</param>
    Public Sub DataReceive(TableElementTypeChar As Char, Number As Integer, Value As Integer) Implements IDirectPlugin.DataReceive
        Dim filterEvents As Boolean = False
        Dim writeLogFile As Boolean = False

        If filterEvents Then
            If TableElementTypeChar = "S" Then
                Return
            ElseIf TableElementTypeChar = "L" Then
                Return
            ElseIf TableElementTypeChar = "M" Then
                Return
            ElseIf TableElementTypeChar = "N" Then
                Return
            ElseIf TableElementTypeChar = "W" Then
                Return
            ElseIf TableElementTypeChar = "B" Then
                Return
            ElseIf TableElementTypeChar = "C" Then
                Return
            End If
        End If

        Dim cmd As String = TableElementTypeChar & Number
        Dim cmdValue As String = cmd & Value
        Dim request As String = String.Format("http://localhost/b2s/cmd?type={0}&number={1}&value={2}", TableElementTypeChar, Number, Value)
        Dim result As String = webClient.DownloadString(request)

        If writeLogFile Then
            sw.WriteLine(
                IIf(fileExists,
                cmdValue, "Start Error Log for today"))
        End If
    End Sub

#End Region

#Region "IDirectPluginPinMame Members"

    ''' <summary>
    ''' This method is called by the B2S.Server, when the Run method of PinMame gets called.<br/>
    ''' The IDirectPlugin interface requires the implementation of this method.<br/>
    ''' </summary>
    Public Sub PinMameRun() Implements IDirectPluginPinMame.PinMameRun

    End Sub

    ''' <summary>
    ''' This method is called by the B2S.Server, when the property Pause of Pinmame gets set to false.<br/>
    ''' The IDirectPlugin interface requires the implementation of this method.<br/>
    ''' </summary>
    Public Sub PinMameContinue() Implements IDirectPluginPinMame.PinMameContinue

    End Sub



    ''' <summary>
    ''' This method is called, when the property Pause of Pinmame gets set to true.<br/>
    ''' The IDirectPlugin interface requires the implementation of this method.<br/>
    ''' </summary>
    Public Sub PinMamePause() Implements IDirectPluginPinMame.PinMamePause

    End Sub


    ''' <summary>
    ''' This method is called by the B2S.Server, when the Stop method of Pinmame is called.<br/>
    ''' The IDirectPlugin interface requires the implementation of this method.<br/>
    ''' </summary>
    Public Sub PinMameStop() Implements IDirectPluginPinMame.PinMameStop

    End Sub




#End Region

#Region "IDirectPluginFrontend Members"

    ''' <summary>
    ''' PluginShowFrontend is called by the B2S.Server if a plugin has to show its frontend.<br />
    ''' The IDirectPluginFrontend interface requires the implementation of this method.
    ''' </summary>
    ''' <param name="Owner">(optional)The owner window for the frontend.<br/>Make sure you also support Nothing for this parameter.</param>
    Public Sub PluginShowFrontend(Optional Owner As Form = Nothing) Implements IDirectPluginFrontend.PluginShowFrontend

        ' Open the frontend in this method

        'Check if the frontend is already open
        'If yes, bring it to the front and set focus
        For Each F As Form In Application.OpenForms
            If TypeOf F Is Frontend Then
                F.BringToFront()
                F.Focus()
                Return
            End If
        Next

        'If the frontend is not yet open, create a new instance and show it
        Dim FE As Frontend = New Frontend()
        If Owner Is Nothing Then
            'Owner para is not set.
            FE.Show()
        Else
            'Owner para set set. Show frontend and pass owner para.
            FE.Show(Owner)
        End If

    End Sub

#End Region

#Region "Constructor of the class"
    ''' <summary>
    ''' Initializes a new instance of the SamplePluginVBNet class.<br/>
    ''' The class exporting the plugin interface must have a constructor without parameters. If you dont want to do any work in the constructor, the constructor can be ommitted.
    ''' </summary>
    Public Sub New()

    End Sub
#End Region

End Class
