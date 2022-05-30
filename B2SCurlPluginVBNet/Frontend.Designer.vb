<Global.Microsoft.VisualBasic.CompilerServices.DesignerGenerated()> _
Partial Class Frontend
    Inherits System.Windows.Forms.Form

    'Das Formular überschreibt den Löschvorgang, um die Komponentenliste zu bereinigen.
    <System.Diagnostics.DebuggerNonUserCode()> _
    Protected Overrides Sub Dispose(ByVal disposing As Boolean)
        Try
            If disposing AndAlso components IsNot Nothing Then
                components.Dispose()
            End If
        Finally
            MyBase.Dispose(disposing)
        End Try
    End Sub

    'Wird vom Windows Form-Designer benötigt.
    Private components As System.ComponentModel.IContainer

    'Hinweis: Die folgende Prozedur ist für den Windows Form-Designer erforderlich.
    'Das Bearbeiten ist mit dem Windows Form-Designer möglich.  
    'Das Bearbeiten mit dem Code-Editor ist nicht möglich.
    <System.Diagnostics.DebuggerStepThrough()> _
    Private Sub InitializeComponent()
        Dim resources As System.ComponentModel.ComponentResourceManager = New System.ComponentModel.ComponentResourceManager(GetType(Frontend))
        Me.label1 = New System.Windows.Forms.Label()
        Me.DocuLinkLabel = New System.Windows.Forms.LinkLabel()
        Me.VersionLabel = New System.Windows.Forms.Label()
        Me.CloseButton = New System.Windows.Forms.Button()
        Me.SuspendLayout()
        '
        'label1
        '
        Me.label1.AutoSize = True
        Me.label1.Font = New System.Drawing.Font("Microsoft Sans Serif", 24.0!, System.Drawing.FontStyle.Bold, System.Drawing.GraphicsUnit.Point, CType(0, Byte))
        Me.label1.Location = New System.Drawing.Point(1, 9)
        Me.label1.Name = "label1"
        Me.label1.Size = New System.Drawing.Size(529, 37)
        Me.label1.TabIndex = 1
        Me.label1.Text = "B2S.Server Sample Plugin VB.net"
        '
        'DocuLinkLabel
        '
        Me.DocuLinkLabel.AutoSize = True
        Me.DocuLinkLabel.Location = New System.Drawing.Point(101, 130)
        Me.DocuLinkLabel.Name = "DocuLinkLabel"
        Me.DocuLinkLabel.Size = New System.Drawing.Size(332, 13)
        Me.DocuLinkLabel.TabIndex = 6
        Me.DocuLinkLabel.TabStop = True
        Me.DocuLinkLabel.Text = "Follow this link for the documention on the B2S.Server Sample Plugin"
        '
        'VersionLabel
        '
        Me.VersionLabel.Location = New System.Drawing.Point(39, 62)
        Me.VersionLabel.Name = "VersionLabel"
        Me.VersionLabel.Size = New System.Drawing.Size(461, 23)
        Me.VersionLabel.TabIndex = 5
        Me.VersionLabel.TextAlign = System.Drawing.ContentAlignment.MiddleCenter
        '
        'CloseButton
        '
        Me.CloseButton.Dock = System.Windows.Forms.DockStyle.Bottom
        Me.CloseButton.Location = New System.Drawing.Point(0, 193)
        Me.CloseButton.Name = "CloseButton"
        Me.CloseButton.Size = New System.Drawing.Size(543, 23)
        Me.CloseButton.TabIndex = 4
        Me.CloseButton.Text = "Close"
        Me.CloseButton.UseVisualStyleBackColor = True
        '
        'Frontend
        '
        Me.AutoScaleDimensions = New System.Drawing.SizeF(6.0!, 13.0!)
        Me.AutoScaleMode = System.Windows.Forms.AutoScaleMode.Font
        Me.ClientSize = New System.Drawing.Size(543, 216)
        Me.Controls.Add(Me.DocuLinkLabel)
        Me.Controls.Add(Me.VersionLabel)
        Me.Controls.Add(Me.CloseButton)
        Me.Controls.Add(Me.label1)
        Me.Icon = CType(resources.GetObject("$this.Icon"), System.Drawing.Icon)
        Me.Name = "Frontend"
        Me.StartPosition = System.Windows.Forms.FormStartPosition.CenterParent
        Me.Text = "B2S.Server Sample Plugin VB.net frontend"
        Me.ResumeLayout(False)
        Me.PerformLayout()

    End Sub
    Private WithEvents label1 As System.Windows.Forms.Label
    Private WithEvents DocuLinkLabel As System.Windows.Forms.LinkLabel
    Private WithEvents VersionLabel As System.Windows.Forms.Label
    Private WithEvents CloseButton As System.Windows.Forms.Button
End Class
