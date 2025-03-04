import React, { Component } from 'react';
import { connect } from 'react-redux';
import ContendCard from '../contents/ContentCard';
import { Link, useNavigate } from 'react-router-dom';

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

import $ from 'jquery';
import BACKEND_ADDRESS_URL from "../config/BackendAddressURLConfig";

class ObjectiveProblemCreate extends Component {
    state = {
        op_description: '',
        op_total_score: 1,
        op_tag: 'None',
        op_correct_answer: '',
        op_use_count: 0,
        op_difficulty: 1,

        error_message: '',

        show_preview: false,
        is_loading: false
    }

    handleSubmit = () => {
        // check submission
        this.setState({
            error_message: '',
            is_loading: true
        });
        if (this.state.op_total_score === '') {
            this.setState({
                error_message: 'The score cannot be empty',
                is_loading: false,
            });
        } else if (parseInt(this.state.op_total_score) <= 0) {
            console.log(this.state.op_total_score);
            this.setState({
                error_message: 'The score must be a positive integer',
                is_loading: false,
            });
        } else if (this.state.op_difficulty === '') {
            this.setState({
                error_message: 'The difficulty cannot be empty',
                is_loading: false,
            });
        } else if (parseInt(this.state.op_difficulty) <= 0 || parseInt(this.state.op_difficulty) > 5) {
            console.log(this.state.op_difficulty);
            this.setState({
                error_message: 'The difficulty must be a positive integer between 1 and 5',
                is_loading: false,
            });
        } else if (this.state.op_tag === '') {
            this.setState({
                error_message: 'The tag cannot be empty',
                is_loading: false,
            });
        } else if (this.state.op_description === '') {
            this.setState({
                error_message: 'The description cannot be empty',
                is_loading: false,
            });
        } else if (this.state.op_correct_answer === '') {
            this.setState({
                error_message: 'The answer cannot be empty',
                is_loading: false,
            });
        } else if (this.state.op_tag.length > 100) {
            this.setState({
                error_message: 'The tag cannot exceed 100 characters, current length: ' + this.state.op_tag.length,
                is_loading: false,
            });
        } else if (this.state.op_description.length > 10000) {
            this.setState({
                error_message: 'The description cannot exceed 10,000 characters',
                is_loading: false,
            });
        } else if (this.state.op_correct_answer.length > 1024) {
            this.setState({
                error_message: 'The answer cannot exceed 1024 characters, current length: ' + this.state.op_correct_answer.length,
                is_loading: false,
            });
        } else {
            // console.log("submit");
            // console.log(this.state.op_total_score);
            // console.log(this.state.op_tag);
            // console.log(this.state.op_description);
            // console.log(this.state.op_correct_answer);
            const token = this.props.token;
            $.ajax({
                url: BACKEND_ADDRESS_URL + "/problem_manage/objective_problem_manage/",
                type: "POST",
                data: {
                    opDescription: this.state.op_description,
                    opTotalScore: this.state.op_total_score,
                    opCorrectAnswer: this.state.op_correct_answer,
                    opTag: this.state.op_tag,
                    opDifficulty: this.state.op_difficulty,
                },
                headers: {
                    Authorization: "Bearer " + token
                },
                success: (resp) => {
                    // console.log(resp);
                    if (resp.error_message === "success") {
                        // navigate
                        this.props.navigate(`/problem_manage/objective_problem_manage/${resp.objective_problem_id}/`);
                    } else {
                        this.setState({
                            error_message: resp.error_message,
                            is_loading: false,
                        });
                    }
                },
            });

        }
    }

    handleLoadingRender = () => {
        if (this.state.is_loading) {
            return (
                <span className="spinner-border spinner-border-sm me-2" aria-hidden="true"></span>
            );
        }
    }

    handleEditorRender = () => {
        const height = "50vh";
        if (this.state.show_preview) {
            return (
                <div className='markdown-body' style={{ overflowY: "auto", maxHeight: height, minHeight: height }}>
                    <Markdown
                        remarkPlugins={[remarkGfm, remarkMath]}
                        rehypePlugins={[rehypeHighlight, rehypeKatex]}
                    >
                        {this.state.op_description}
                    </Markdown>
                </div>
            );
        } else {
            return (
                <CodeMirror
                    value={this.state.op_description}
                    height={height}
                    extensions={[markdown({ base: markdownLanguage, codeLanguages: languages }), EditorView.lineWrapping]}
                    onChange={(op_description) => { this.setState({ op_description: op_description }) }}
                    basicSetup={{
                        tabSize: 4
                    }}
                />
            );
        }
    }

    handlePermissionRender = () => {
        if (this.props.permission > 0) {
            return (
                <div className="container">
                    <ContendCard>
                        <Link className='btn btn-outline-primary' to={'/problem_manage/objective_problem_manage/'}>
                            Back
                        </Link>
                        <button className='btn btn-success float-end' onClick={() => this.handleSubmit()} disabled={this.state.is_loading}>
                            {this.handleLoadingRender()}
                            Submit
                        </button>
                        <hr />
                        <div className='row'>
                            <div className='col-md-4'>
                                <h4>Create an Objective Problem</h4>
                            </div>
                            <div className='col-md-8 text-md-end' style={{ color: "red" }}>
                                {this.state.error_message}
                            </div>
                        </div>

                        <div className="row mb-2">
                            <div className="col-md-3">
                                <div className="row">
                                    <div className="col-auto">
                                        <label htmlFor="op_author_name" className="col-form-label">Author</label>
                                    </div>
                                    <div className="col">
                                        <input type="text" className="form-control" id="op_author_name" defaultValue={this.props.name} disabled />
                                    </div>
                                </div>
                            </div>
                            <div className="col-md-3">
                                <div className="row">
                                    <div className="col-auto">
                                        <label htmlFor="op_tag" className="col-form-label">Tag</label>
                                    </div>
                                    <div className="col">
                                        <input type="text" className="form-control" id="op_tag" defaultValue={this.state.op_tag} onChange={(e) => { this.setState({ op_tag: e.target.value }); }} />
                                    </div>
                                </div>
                            </div>
                            <div className="col-md-2">
                                <div className="row">
                                    <div className="col-auto">
                                        <label htmlFor="op_total_score" className="col-form-label">Score</label>
                                    </div>
                                    <div className="col">
                                        <input type="number" className="form-control" id="op_total_score" defaultValue={this.state.op_total_score} onChange={(e) => { this.setState({ op_total_score: e.target.value }); }} min={1} />
                                    </div>
                                </div>
                            </div>
                            <div className="col-md-2">
                                <div className="row">
                                    <div className="col-auto">
                                        <label htmlFor="op_difficulty" className="col-form-label">Difficulty</label>
                                    </div>
                                    <div className="col">
                                        <input type="number" className="form-control" id="op_difficulty" defaultValue={this.state.op_difficulty} onChange={(e) => { this.setState({ op_difficulty: e.target.value }); }} min={1} max={5} />
                                    </div>
                                </div>
                            </div>
                            <div className="col-md-2">
                                <div className="row">
                                    <div className="col-auto">
                                        <label htmlFor="op_use_count" className="col-form-label">Use Count</label>
                                    </div>
                                    <div className="col">
                                        <input type="text" className="form-control" id="op_use_count" defaultValue={this.state.op_use_count} disabled />
                                    </div>
                                </div>
                            </div>
                        </div>

                        <div className="row mb-2">
                            <div className="col-md-3">
                                <span>Description</span>
                            </div>
                            <div className="col-md-9 justify-content-md-end d-flex">
                                <div className="me-3">
                                    Word Count: {this.state.op_description.length} / 10000
                                </div>
                                <div className="form-check form-switch">
                                    <input className="form-check-input" type="checkbox" role="switch" id="showPreview" onChange={(e) => { this.setState({ show_preview: !this.state.show_preview }) }} />
                                    <label className="form-check-label" htmlFor="showPreview">Preview</label>
                                </div>
                            </div>
                            {this.handleEditorRender()}
                        </div>

                        <div className="row">
                            <div className="col">
                                <div className="row">
                                    <div className="col-auto">
                                        <label htmlFor="op_correct_answer" className="col-form-label">Answer</label>
                                    </div>
                                    <div className="col">
                                        <input type="text" className="form-control" id="op_correct_answer" defaultValue={this.state.op_correct_answer} onChange={(e) => { this.setState({ op_correct_answer: e.target.value }); }} />
                                    </div>
                                </div>
                            </div>
                        </div>
                    </ContendCard>
                </div>
            );
        } else {
            return (
                <div className="container">
                    <div className="row justify-content-md-center align-items-center" style={{ height: "90vh" }}>
                        <div className="col col-md-7">
                            <ContendCard>
                                <div className="row justify-content-md-center">
                                    <h1 className="text-center">
                                        Create an Objective Problem
                                    </h1>
                                    <hr />
                                    <h4 className="text-center" style={{ textDecoration: "none" }}>You do not have permission to access this page</h4>
                                </div>
                            </ContendCard>
                        </div>
                    </div>
                </div>

            );
        }
    }

    handleAccountRender = () => {
        if (this.props.is_login) {
            return (
                <React.Fragment>
                    {this.handlePermissionRender()}
                </React.Fragment>
            );
        } else {
            return (
                <div className="container">
                    <div className="row justify-content-md-center align-items-center" style={{ height: "90vh" }}>
                        <div className="col col-md-7">
                            <ContendCard>
                                <div className="row justify-content-md-center">
                                    <h1 className="text-center">
                                        Create an Objective Problem
                                    </h1>
                                    <hr />
                                    <h4 className="text-center">Please <Link className='btn btn-link px-0' to="/login/" style={{ textDecoration: "none" }}><h4 className='mb-1'>sign in</h4></Link> to access</h4>
                                </div>
                            </ContendCard>
                        </div>
                    </div>
                </div>
            );
        }
    }

    render() {
        return (
            <React.Fragment>
                {this.handleAccountRender()}
            </React.Fragment>
        );
    }
}

const mapStateToProps = (state, props) => {
    return {
        ...props,
        user_id: state.user_id,
        username: state.username,
        name: state.name,
        permission: state.permission,
        token: state.token,
        is_login: state.is_login,
    };
};

export default connect(mapStateToProps, null)((props) =>
    <ObjectiveProblemCreate
        {...props}
        navigate={useNavigate()}
    />
);