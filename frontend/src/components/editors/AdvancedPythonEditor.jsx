import React, { Component } from 'react';
import Editor from '@monaco-editor/react';

class AdvancedPythonEditor extends Component {
    state = {
        code: `print("Hello World")`
    }
    render() {
        return (
            <Editor height="50vh" defaultLanguage="python" defaultValue={this.state.code} />
        );
    }
}

export default AdvancedPythonEditor;