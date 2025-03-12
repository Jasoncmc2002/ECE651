import React, { Component } from 'react';
import Editor from 'react-simple-code-editor';
import { highlight, languages } from 'prismjs/components/prism-core';
import 'prismjs/components/prism-clike';
import 'prismjs/components/prism-python';
import 'prismjs/themes/prism.css'; //Example style, you can use another

class SimplePythonEditor extends Component {
    state = {
        code: `print("Hello World")`,
    }

    componentDidMount() {
    }

    render() {
        return (
            <Editor
                value={this.state.code}
                onValueChange={code => this.setState({ code: code })}
                highlight={code => highlight(code, languages.python)}
                padding={10}
                style={{
                    fontFamily: '"Fira code", "Fira Mono", monospace',
                    fontSize: 12,
                }}
                tabSize={4}
                insertSpaces
            />
        );
    }
}

export default SimplePythonEditor;