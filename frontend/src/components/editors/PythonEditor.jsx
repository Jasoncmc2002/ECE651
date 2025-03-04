import React, { Component } from 'react';
import CodeMirror from '@uiw/react-codemirror';
import { python } from '@codemirror/lang-python';

class PythonEditor extends Component {
    state = {
        code: `print("Hello World")`
    }

    handleEditorChange = (value) => {
        console.log(value);
    }

    render() {
        return (
            <CodeMirror
                value={this.state.code}
                height="200px"
                extensions={[python()]}
                onChange={this.handleEditorChange}
                basicSetup={{  // API: https://www.npmjs.com/package/@uiw/codemirror-extensions-basic-setup
                    tabSize: 4
                }}
            // readOnly={true}
            // editable={false}
            />
        );
    }
}

export default PythonEditor;