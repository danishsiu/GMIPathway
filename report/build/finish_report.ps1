# Opens the report in Word, fills in the contents pages, figure numbers and page numbers,
# saves it, and exports a PDF for checking.
param(
    [Parameter(Mandatory = $true)][string]$DocxPath,
    [Parameter(Mandatory = $true)][string]$PdfPath
)

$word = New-Object -ComObject Word.Application
$word.Visible = $false
$word.DisplayAlerts = 0
try {
    $document = $word.Documents.Open($DocxPath, $false, $false, $false)
    $document.Repaginate()
    [void]$document.Fields.Update()
    foreach ($table in $document.TablesOfContents) { $table.Update() }
    foreach ($table in $document.TablesOfFigures) { $table.Update() }
    $document.Repaginate()
    foreach ($table in $document.TablesOfContents) { $table.UpdatePageNumbers() }
    foreach ($table in $document.TablesOfFigures) { $table.UpdatePageNumbers() }
    $document.Save()
    $document.ExportAsFixedFormat($PdfPath, 17)
    Write-Output ("Pages: " + $document.ComputeStatistics(2))
    Write-Output ("Contents tables: " + $document.TablesOfContents.Count + ", figure tables: " + $document.TablesOfFigures.Count)
    $document.Close(0)
}
finally {
    $word.Quit()
}
