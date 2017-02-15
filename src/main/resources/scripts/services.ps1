$header="hostname,servicename,status,port`n"
$services = Get-Service | Where-Object {$_.Name -match ".*hys.*" -or $_.Name -match ".*ora.*"} | ForEach-Object {
   $servicename =  $_.name
   $id = Get-WmiObject -Class Win32_Service -Filter "Name LIKE '$servicename'" | Select-Object -ExpandProperty ProcessId
   $output = ''
	if($id -ne 0 -and $id ){
		$data = netstat -ano | findstr $id
		if($data){
		$data = $data[0..$data.count]
        foreach ($line in $data)
        {
            $line = $line -replace '^\s+', ''
            $line = $line -split '\s+'
			if(($line[1] -split ":")[1]){
		    $output += ($line[1] -split ":")[1]+";"
			}
        }		
 	}
 }
$env:computername, $_.name, $_.status, $output -join ',' 
}
$services = $services -join "`n"

Out-File -Encoding "UTF8" -FilePath $args[0] -InputObject $header$services
