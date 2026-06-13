# 加载 Windows UI Automation 程序集
Add-Type -AssemblyName UIAutomationClient
Add-Type -AssemblyName UIAutomationTypes
Add-Type -AssemblyName System.Windows.Forms

Write-Host "=== 脚本将在 2 秒后开始扫描 ===" -ForegroundColor Cyan
Start-Sleep -Seconds 2

Write-Host "=== 开始扫描（持续 3 秒）... 请将鼠标移至计算器各个按钮上 ===" -ForegroundColor Green

$stopwatch = [System.Diagnostics.Stopwatch]::StartNew()
# 使用哈希表进行数据整合与去重
$uniqueElements = @{}

while ($stopwatch.Elapsed.TotalSeconds -lt 3) {
    $pos = [System.Windows.Forms.Cursor]::Position
    try {
        $point = New-Object System.Windows.Point($pos.X, $pos.Y)
        $element = [System.Windows.Automation.AutomationElement]::FromPoint($point)

        if ($element -ne $null) {
            # 提取核心属性
            $autoId    = if ($element.Current.AutomationId) { $element.Current.AutomationId } else { "[Null]" }
            $name      = if ($element.Current.Name) { $element.Current.Name } else { "[Null]" }
            $className = if ($element.Current.ClassName) { $element.Current.ClassName } else { "[Null]" }
            $controlType = $element.Current.LocalizedControlType

            # 生成唯一 Key 用于去重
            $key = "$autoId|$name|$className"

            if (-not $uniqueElements.ContainsKey($key)) {
                $uniqueElements[$key] = @{
                    "AutomationId" = $autoId
                    "Name"         = $name
                    "ClassName"    = $className
                    "ControlType"  = $controlType
                }
            }
        }
    }
    catch {
        # 忽略瞬时或无权限窗口的错误
    }
    Start-Sleep -Milliseconds 50
}
$stopwatch.Stop()

# --- 最终整合输出 ---
Write-Host "`n=== 扫描结束！以下是去重后的元素整合报告 ===" -ForegroundColor Cyan
Write-Host "共捕获到 $($uniqueElements.Count) 个唯一元素:`n" -ForegroundColor Cyan

foreach ($item in $uniqueElements.Values) {
    Write-Host "------------------------------------" -ForegroundColor Gray
    Write-Host "ControlType   : " -NoNewline; Write-Host $item.ControlType -ForegroundColor Magenta
    Write-Host "AutomationId  : " -NoNewline; Write-Host $item.AutomationId -ForegroundColor White
    Write-Host "Name          : " -NoNewline; Write-Host $item.Name -ForegroundColor Yellow
    Write-Host "ClassName     : " -NoNewline; Write-Host $item.ClassName -ForegroundColor DarkGray
}
Write-Host "------------------------------------" -ForegroundColor Gray