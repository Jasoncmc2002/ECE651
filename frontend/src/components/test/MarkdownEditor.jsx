import React, { Component } from 'react';
import ContendCard from '../contents/ContentCard';

import Markdown from 'react-markdown';
import rehypeHighlight from 'rehype-highlight';  // highlight code, this will only add class name to code keyword span, you also need a css to control the style
import remarkGfm from 'remark-gfm';  // render table
import rehypeKatex from 'rehype-katex';  // math
import remarkMath from 'remark-math';  // math
import 'katex/dist/katex.min.css';   // math
import 'github-markdown-css';  // github markdown css to control the rendered markdown. Make sure the div enclosing the Markdown has class name: className='markdown-body'
import '../../css/github_highlight.css';  // github syntax highlight theme
// import rehypeRaw from 'rehype-raw';  // html, package not installed
// import rehypeSanitize from 'rehype-sanitize';  // html security, package not installed

import CodeMirror from '@uiw/react-codemirror';
import { markdown, markdownLanguage } from '@codemirror/lang-markdown';
import { languages } from '@codemirror/language-data';
import { EditorView } from '@codemirror/view';

class MarkdownEditor extends Component {
    state = {
        code: ``
    }
    render() {
        return (
            <div className="container">
                <ContendCard>
                    <div className="row">
                        <div className="col col-md-6">
                            <h1 className='text-center'>Editor</h1>
                            <hr />
                            <CodeMirror
                                value={this.state.code}
                                height='75vh'
                                extensions={[markdown({ base: markdownLanguage, codeLanguages: languages }), EditorView.lineWrapping]}
                                onChange={(code) => { this.setState({ code: code }) }}
                                basicSetup={{
                                    tabSize: 4
                                }}
                            />
                        </div>
                        <div className="col col-md-6">
                            <h1 className='text-center'>Preview</h1>
                            <hr />
                            <div className='markdown-body' style={{ overflowY: "auto", maxHeight: "75vh", minHeight: "75vh" }}>
                                <Markdown
                                    remarkPlugins={[remarkGfm, remarkMath]}
                                    rehypePlugins={[rehypeHighlight, rehypeKatex]}
                                >
                                    {this.state.code}
                                </Markdown>
                            </div>
                        </div>
                    </div>
                </ContendCard>
            </div>
        );
    }
}

export default MarkdownEditor;