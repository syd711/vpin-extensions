''' <summary>
''' Frontend for the B2S.Sever Sample Plugin BV.net
''' </summary>
Public Class Frontend

    ''' <summary>
    ''' Handles the Click event of the CloseButton control.
    ''' </summary>
    ''' <param name="sender">The source of the event.</param>
    ''' <param name="e">The <see cref="System.EventArgs"/> instance containing the event data.</param>
    Private Sub CloseButton_Click(sender As System.Object, e As System.EventArgs) Handles CloseButton.Click
        Me.Close()
    End Sub

    ''' <summary>
    ''' Handles the LinkClicked event of the DocuLinkLabel control.
    ''' </summary>
    ''' <param name="sender">The source of the event.</param>
    ''' <param name="e">The <see cref="System.Windows.Forms.LinkLabelLinkClickedEventArgs"/> instance containing the event data.</param>
    Private Sub DocuLinkLabel_LinkClicked(sender As System.Object, e As System.Windows.Forms.LinkLabelLinkClickedEventArgs) Handles DocuLinkLabel.LinkClicked
        System.Diagnostics.Process.Start("http://directoutput.github.io/B2SServerSamplePlugin/")
    End Sub

    ''' <summary>
    ''' Handles the Load event of the Frontend control.
    ''' </summary>
    ''' <param name="sender">The source of the event.</param>
    ''' <param name="e">The <see cref="System.EventArgs"/> instance containing the event data.</param>
    Private Sub Frontend_Load(sender As System.Object, e As System.EventArgs) Handles MyBase.Load
        'Get the version of the assembly
        Dim V As Version = System.Reflection.Assembly.GetExecutingAssembly().GetName().Version
        'Calculate the BuildDate based in the build and revsion number of the project.
        Dim BuildDate As DateTime = New DateTime(2000, 1, 1).AddDays(V.Build).AddSeconds(V.Revision * 2)
        'Format and set the name string.
        VersionLabel.Text = String.Format("Sample Plugin VB.net (V: {0} as of {1})", V.ToString(), BuildDate.ToString("yyyy.MM.dd hh:mm"))
    End Sub
End Class