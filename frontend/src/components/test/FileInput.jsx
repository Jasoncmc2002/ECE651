// src/FileInput.js
// https://developer.mozilla.org/en-US/docs/Web/API/File_API
import React from 'react';
import * as XLSX from 'xlsx';

function FileInput() {
    const [data, setData] = React.useState(null);

    const handleFileUpload = (e) => {
        const file = e.target.files[0];
        // console.log(file.size);
        const reader = new FileReader();
        // https://developer.mozilla.org/en-US/docs/Web/API/FileReader

        reader.onload = (event) => {
            const workbook = XLSX.read(event.target.result, { type: 'binary' });
            const sheetName = workbook.SheetNames[0];
            const sheet = workbook.Sheets[sheetName];
            const sheetData = XLSX.utils.sheet_to_json(sheet);
            // console.log(sheetData);

            setData(sheetData);
        };

        if (file) {
            reader.readAsArrayBuffer(file);
        }
    };

    return (
        <div>
            <input type="file" onChange={handleFileUpload} />
            {data && (
                <div>
                    <h2>Imported Data:</h2>
                    <pre>{JSON.stringify(data, null, 2)}</pre>
                    {/* https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Global_Objects/JSON/stringify */}
                </div>
            )}
        </div>
    );
}

export default FileInput;