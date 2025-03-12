import React, { Component } from 'react';
import ContendCard from '../contents/ContentCard';
import SimplePythonEditor from '../editors/SimplePythonEditor';
import AdvancedPythonEditor from '../editors/AdvancedPythonEditor';
import PythonEditor from '../editors/PythonEditor';
import Markdown from 'react-markdown'
import FileInput from './FileInput';
import DismissibleExample from './DismissibleExample';
import PlacementExample from './PlacementExample';

class EditorDemo extends Component {
    state = {
        markdown: `# Hi, *Pluto*!`,
    }
    render() {
        return (
            <div className="container">
                <h1>Toast</h1>
                <DismissibleExample />
                <hr />
                <PlacementExample />
                <hr />
                <h1 className='text-center'>Python Editors</h1>
                <hr />
                <h4>Powered by: <a href='https://www.npmjs.com/package/react-simple-code-editor' target='_blank' rel='noreferrer'>react-simple-code-editor</a></h4>
                <ContendCard>
                    <SimplePythonEditor />
                </ContendCard>
                <hr className="mt-2" />
                <h4>Powered by: <a href='https://www.npmjs.com/package/@monaco-editor/react' target='_blank' rel='noreferrer'>@monaco-editor/react</a></h4>
                <ContendCard>
                    <AdvancedPythonEditor />
                </ContendCard>
                <hr className="mt-2" />
                <h4>Powered by: <a href='https://www.npmjs.com/package/@uiw/react-codemirror' target='_blank' rel='noreferrer'>@uiw/react-codemirror
                </a></h4>
                <ContendCard>
                    <PythonEditor />
                </ContendCard>
                <hr className="mt-2" />
                <h4>Powered by: <a href='https://www.npmjs.com/package/react-markdown' target='_blank' rel='noreferrer'>react-markdown
                </a></h4>
                <ContendCard>
                    <Markdown>{this.state.markdown}</Markdown>
                </ContendCard>

                <hr />
                <h1>File Input Component Test</h1>
                <ContendCard>
                    <FileInput />
                </ContendCard>
            </div>
        );
    }
}

export default EditorDemo;