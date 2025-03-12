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
import { python } from '@codemirror/lang-python';
import { languages } from '@codemirror/language-data';
import { EditorView } from '@codemirror/view';

import $ from 'jquery';
import BACKEND_ADDRESS_URL from "../config/BackendAddressURLConfig";

import { ArrowRight } from 'react-bootstrap-icons';

class ProgrammingCreate extends Component {
    state = {
        p_description: '',  // 10000
        p_total_score: 1,
        p_tag: 'None',  // 100
        p_title: '',  // 100
        time_limit: 400,  // 单位ms
        code_size_limit: 16,  // 单位kb
        p_use_count: 0,
        p_difficulty: 1,
        p_judge_code: '# Student answers will be placed on top of the judge code',  // 160000

        test_case_list: [
            // { test_case_id: "1", tc_input: "1 2", tc_output: "3" },
            // { test_case_id: "2", tc_input: "3 4", tc_output: "7" },
            // { test_case_id: "3", tc_input: "5 6", tc_output: "11" },
        ],
        test_case_fake_uid: 0,
        new_tc_input: '',  // 1024
        new_tc_output: '',  // 1024

        error_message: '',

        show_preview: false,
        is_loading: false
    }

    handleSubmit = () => {
        // console.log("submit");
        this.setState({
            error_message: '',
            is_loading: true
        });
        // 检查输入
        // 检查数字类输入
        if (this.state.p_total_score === '') {
            this.setState({
                error_message: 'The score cannot be empty',
                is_loading: false,
            });
        } else if (parseInt(this.state.p_total_score) <= 0) {
            console.log(this.state.p_total_score);
            this.setState({
                error_message: 'The score must be a positive integer',
                is_loading: false,
            });
        } else if (this.state.p_difficulty === '') {
            this.setState({
                error_message: 'The difficulty cannot be empty',
                is_loading: false,
            });
        } else if (parseInt(this.state.p_difficulty) <= 0 || parseInt(this.state.p_difficulty) > 5) {
            console.log(this.state.p_difficulty);
            this.setState({
                error_message: 'The difficulty must be a positive integer between 1 and 5',
                is_loading: false,
            });
        } else if (this.state.time_limit === '') {
            this.setState({
                error_message: 'The time limit cannot be empty',
                is_loading: false,
            });
        } else if (parseInt(this.state.time_limit) <= 0) {
            console.log(this.state.time_limit);
            this.setState({
                error_message: 'The time limit must be a positive integer',
                is_loading: false,
            });
        } else if (this.state.code_size_limit === '') {
            this.setState({
                error_message: 'The code size limit cannot be empty',
                is_loading: false,
            });
        } else if (parseInt(this.state.code_size_limit) <= 0) {
            console.log(this.state.code_size_limit);
            this.setState({
                error_message: 'The code size limit must be a positive integer',
                is_loading: false,
            });
        } else if (this.state.p_description === '') {  // 检查字符串类输入
            this.setState({
                error_message: 'The description cannot be empty',
                is_loading: false,
            });
        } else if (this.state.p_tag === '') {
            this.setState({
                error_message: 'The tag cannot be empty',
                is_loading: false,
            });
        } else if (this.state.p_title === '') {
            this.setState({
                error_message: 'The title cannot be empty',
                is_loading: false,
            });
        } else if (this.state.p_description.length > 10000) {
            this.setState({
                error_message: 'The description cannot exceed 10,000 characters',
                is_loading: false,
            });
        } else if (this.state.p_tag.length > 100) {
            this.setState({
                error_message: 'The tag cannot exceed 100 characters, current length: ' + this.state.p_tag.length,
                is_loading: false,
            });
        } else if (this.state.p_title.length > 100) {
            this.setState({
                error_message: 'The title cannot exceed 100 characters, current length: ' + this.state.p_title.length,
                is_loading: false,
            });
        } else if (this.state.test_case_list.length === 0) {  // 检查测试用例列表
            this.setState({
                error_message: 'The test case cannot be empty',
                is_loading: false,
            });
        } else if (this.state.p_judge_code.length > 16000) {
            this.setState({
                error_message: 'The judge code cannot exceed 16,000 characters, current length: ' + this.state.p_judge_code.length,
                is_loading: false,
            });
        } else {  // 联网提交
            // console.log("submit");
            // console.log(this.state);
            const token = this.props.token;
            // console.log(token);
            // 先提交创建编程题，再提交创建测试用例
            $.ajax({
                url: BACKEND_ADDRESS_URL + "/problem_manage/programming_manage/",
                type: "POST",
                data: {
                    pDescription: this.state.p_description,
                    pTotalScore: this.state.p_total_score,
                    timeLimit: this.state.time_limit,
                    codeSizeLimit: this.state.code_size_limit,
                    pTag: this.state.p_tag,
                    pTitle: this.state.p_title,
                    pJudgeCode: this.state.p_judge_code,
                    pDifficulty: this.state.p_difficulty,
                },
                headers: {
                    Authorization: "Bearer " + token
                },
                success: (resp) => {
                    // console.log(resp);
                    if (resp.error_message === "success") {
                        const created_programming_id = resp.programming_id;
                        // 提交测试用例创建
                        this.state.test_case_list.forEach((test_case) => {
                            $.ajax({
                                url: BACKEND_ADDRESS_URL + "/problem_manage/test_case_manage/",
                                type: "POST",
                                data: {
                                    programmingId: created_programming_id,
                                    tcInput: test_case.tc_input,
                                    tcOutput: test_case.tc_output,
                                    respId: 'no'
                                },
                                headers: {
                                    Authorization: "Bearer " + token
                                },
                                success: (resp) => {
                                    // console.log(resp);
                                    if (resp.error_message === "success") {
                                        // navigate
                                        this.props.navigate(`/problem_manage/programming_manage/${created_programming_id}/`);
                                    }
                                }
                            });
                        });
                    } else {
                        this.setState({
                            error_message: resp.error_message,
                            is_loading: false,
                        });
                    }
                }
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

    handleJudgeCodeEditorRender = () => {
        const height = "46vh";
        return (
            <div className="accordion mt-2" id="judgeCode">
                <div className="accordion-item">
                    <h2 className="accordion-header">
                        <button className="accordion-button collapsed" type="button" data-bs-toggle="collapse" data-bs-target="#collapseJudgeCode" aria-expanded="false" aria-controls="collapseJudgeCode">
                            Code Completion Judge Code
                        </button>
                    </h2>
                    <div id="collapseJudgeCode" className="accordion-collapse collapse" data-bs-parent="#judgeCode">
                        <div className="accordion-body">
                            <CodeMirror
                                value={this.state.p_judge_code}
                                height={height}
                                extensions={[python()]}
                                onChange={(p_judge_code) => { this.setState({ p_judge_code: p_judge_code }) }}
                                basicSetup={{
                                    tabSize: 4
                                }}
                            />
                        </div>
                    </div>
                </div>
            </div>
        );
    }

    handleTestCaseRender = () => {
        const width = "29vw";
        const hight = "10vh";
        const button_width = "5vw";
        return (
            <div className="accordion mt-2" id="testCase">
                <div className="accordion-item">
                    <h2 className="accordion-header">
                        <button className="accordion-button collapsed" type="button" data-bs-toggle="collapse" data-bs-target="#collapseTestCase" aria-expanded="false" aria-controls="collapseTestCase">
                            Test Case
                        </button>
                    </h2>
                    <div id="collapseTestCase" className="accordion-collapse collapse" data-bs-parent="#testCase">
                        <div className="accordion-body">
                            <table className="table">
                                <thead>
                                    <tr>
                                        {/* <th scope="col">伪编号</th> */}
                                        <th scope="col">Input</th>
                                        <th scope="col">Output</th>
                                        <th scope="col">Operation</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    {this.state.test_case_list.map((test_case) => {
                                        return (
                                            <tr key={'tc' + test_case.test_case_id}>
                                                {/* <td>
                                                    {test_case.test_case_id}
                                                </td> */}
                                                <td style={{ minWidth: width, maxWidth: width }}>
                                                    <CodeMirror
                                                        value={test_case.tc_input}
                                                        height={hight}
                                                        editable={false}
                                                        readOnly={true}
                                                        basicSetup={{
                                                            highlightActiveLineGutter: false,
                                                            highlightActiveLine: false
                                                        }}
                                                    />
                                                </td>
                                                <td style={{ minWidth: width, maxWidth: width }}>
                                                    <CodeMirror
                                                        value={test_case.tc_output}
                                                        height={hight}
                                                        editable={false}
                                                        readOnly={true}
                                                        basicSetup={{
                                                            highlightActiveLineGutter: false,
                                                            highlightActiveLine: false
                                                        }}
                                                    />
                                                </td>
                                                <td style={{ minWidth: button_width }}>
                                                    <button
                                                        className="btn btn-outline-danger"
                                                        onClick={() => {
                                                            // console.log(this.state.test_case_list.indexOf(test_case));
                                                            const index = this.state.test_case_list.indexOf(test_case);
                                                            // Do NOT use slice!!!
                                                            const new_test_case_list = this.state.test_case_list.toSpliced(index, 1);
                                                            this.setState({ test_case_list: new_test_case_list });
                                                        }}
                                                        disabled={this.state.is_loading}
                                                    >
                                                        {this.handleLoadingRender()}
                                                        Delete
                                                    </button>
                                                </td>
                                            </tr>
                                        );
                                    })}

                                    {/* the add test case row */}
                                    <tr>
                                        {/* <td>
                                            {this.state.test_case_fake_uid}
                                        </td> */}
                                        <td style={{ minWidth: width, maxWidth: width }}>
                                            <CodeMirror
                                                value={this.state.new_tc_input}
                                                height={hight}
                                                onChange={(new_tc_input) => { this.setState({ new_tc_input: new_tc_input }) }}
                                            />
                                        </td>
                                        <td style={{ minWidth: width, maxWidth: width }}>
                                            <CodeMirror
                                                value={this.state.new_tc_output}
                                                height={hight}
                                                onChange={(new_tc_output) => { this.setState({ new_tc_output: new_tc_output }) }}
                                            />
                                        </td>
                                        <td style={{ minWidth: button_width }}>
                                            <div className='mb-2'>
                                                <button
                                                    className="btn btn-outline-success"
                                                    onClick={() => {
                                                        // console.log('add');
                                                        // console.log(this.state.new_tc_input);
                                                        // console.log(this.state.new_tc_output);
                                                        this.setState({ error_message: '' });
                                                        // 检查输出不能为空，否则测试用例没有意义
                                                        if (this.state.new_tc_output === '') {
                                                            this.setState({ error_message: 'The output cannot be empty' });
                                                        } else if (this.state.new_tc_input !== '' && this.state.new_tc_input.length > 1024) {
                                                            this.setState({ error_message: 'The input cannot exceed 1024 characters, current length: ' + this.state.new_tc_input.length });
                                                        } else if (this.state.new_tc_output.length > 1024) {
                                                            this.setState({ error_message: 'The output cannot exceed 1024 characters, current length: ' + this.state.new_tc_output.length });
                                                        } else {
                                                            const new_test_case_list = this.state.test_case_list  // const 引用，引用指向不能变，但是指向的对象可以变
                                                            new_test_case_list.push({
                                                                test_case_id: this.state.test_case_fake_uid,
                                                                tc_input: this.state.new_tc_input,
                                                                tc_output: this.state.new_tc_output
                                                            });
                                                            this.setState({
                                                                test_case_list: new_test_case_list,
                                                                test_case_fake_uid: this.state.test_case_fake_uid + 1
                                                            });
                                                        }
                                                    }}
                                                    disabled={this.state.is_loading}
                                                >
                                                    {this.handleLoadingRender()}
                                                    Add
                                                </button>
                                            </div>
                                            <div>
                                                <button
                                                    className='btn btn-outline-secondary me-md-2'
                                                    onClick={() => {
                                                        this.setState({
                                                            error_message: '',
                                                            new_tc_input: '',
                                                            new_tc_output: ''
                                                        });
                                                    }}
                                                    disabled={this.state.is_loading}
                                                >
                                                    {this.handleLoadingRender()}
                                                    Clear
                                                </button>
                                            </div>
                                        </td>
                                    </tr>
                                </tbody>
                            </table>

                            <div className="row">
                                <div className="col-md-6">
                                    Test Case Count: {this.state.test_case_list.length}
                                </div>
                                <div className="col-md-6 d-flex justify-content-end">
                                    <span className='align-middle' style={{ color: "red" }}>
                                        {this.state.error_message}
                                    </span>
                                    <Link className='icon-link icon-link-hover ms-2' to="/programming_editor_demo/" target='_blank' style={{ "--bs-icon-link-transform": "translate3d(.25em, 0, 0)" }}>
                                        Run Your Test Case
                                        <ArrowRight />
                                    </Link>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        );
    }

    handleEditorRender = () => {
        const height = "46vh";
        if (this.state.show_preview) {
            return (
                <div className='markdown-body' style={{ overflowY: "auto", maxHeight: height, minHeight: height }}>
                    <Markdown
                        remarkPlugins={[remarkGfm, remarkMath]}
                        rehypePlugins={[rehypeHighlight, rehypeKatex]}
                    >
                        {this.state.p_description}
                    </Markdown>
                </div>
            );
        } else {
            return (
                <CodeMirror
                    value={this.state.p_description}
                    height={height}
                    extensions={[markdown({ base: markdownLanguage, codeLanguages: languages }), EditorView.lineWrapping]}
                    onChange={(p_description) => { this.setState({ p_description: p_description }) }}
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
                        <Link className='btn btn-outline-primary' to={'/problem_manage/programming_manage/'}>
                            Back
                        </Link>
                        <button className='btn btn-success float-end' onClick={() => this.handleSubmit()} disabled={this.state.is_loading}>
                            {this.handleLoadingRender()}
                            Submit
                        </button>
                        <hr />
                        <div className='row'>
                            <div className='col-md-4'>
                                <h4>Create a Programming Problem</h4>
                            </div>
                            <div className='col-md-8 text-md-end' style={{ color: "red" }}>
                                {this.state.error_message}
                            </div>
                        </div>

                        <div className="row mb-2">
                            <div className="col-md-6">
                                <div className="row">
                                    <div className="col-auto">
                                        <label htmlFor="p_title" className="col-form-label">Title</label>
                                    </div>
                                    <div className="col">
                                        <input type="text" className="form-control" id="p_title" defaultValue={this.state.p_title} onChange={(e) => { this.setState({ p_title: e.target.value }); }} />
                                    </div>
                                </div>
                            </div>
                            <div className="col-md-2">
                                <div className="row">
                                    <div className="col-auto">
                                        <label htmlFor="p_total_score" className="col-form-label">Score</label>
                                    </div>
                                    <div className="col">
                                        <input type="number" className="form-control" id="p_total_score" defaultValue={this.state.p_total_score} onChange={(e) => { this.setState({ p_total_score: e.target.value }); }} min={1} />
                                    </div>
                                </div>
                            </div>
                            <div className="col-md-2">
                                <div className="row">
                                    <div className="col-auto">
                                        <label htmlFor="p_difficulty" className="col-form-label">Difficulty</label>
                                    </div>
                                    <div className="col">
                                        <input type="number" className="form-control" id="p_difficulty" defaultValue={this.state.p_difficulty} onChange={(e) => { this.setState({ p_difficulty: e.target.value }); }} min={1} max={5} />
                                    </div>
                                </div>
                            </div>
                            <div className="col-md-2">
                                <div className="row">
                                    <div className="col-auto">
                                        <label htmlFor="p_use_count" className="col-form-label">Use Count</label>
                                    </div>
                                    <div className="col">
                                        <input type="text" className="form-control" id="p_use_count" defaultValue={this.state.p_use_count} disabled />
                                    </div>
                                </div>
                            </div>
                        </div>

                        <div className="row mb-2">
                            <div className="col-md-3">
                                <div className="row">
                                    <div className="col-auto">
                                        <label htmlFor="p_author_name" className="col-form-label">Author</label>
                                    </div>
                                    <div className="col">
                                        <input type="text" className="form-control" id="p_author_name" defaultValue={this.props.name} disabled />
                                    </div>
                                </div>
                            </div>
                            <div className="col-md-3">
                                <div className="row">
                                    <div className="col-auto">
                                        <label htmlFor="p_tag" className="col-form-label">Tag</label>
                                    </div>
                                    <div className="col">
                                        <input type="text" className="form-control" id="p_tag" defaultValue={this.state.p_tag} onChange={(e) => { this.setState({ p_tag: e.target.value }); }} />
                                    </div>
                                </div>
                            </div>
                            <div className="col-md-3">
                                <div className="row">
                                    <div className="col-auto">
                                        <label htmlFor="time_limit" className="col-form-label">Time Limit</label>
                                    </div>
                                    <div className="col">
                                        <div className="input-group">
                                            <input type="number" className="form-control" id="time_limit" defaultValue={this.state.time_limit} onChange={(e) => { this.setState({ time_limit: e.target.value }); }} min={150} max={2000} />
                                            <span className="input-group-text">ms</span>
                                        </div>
                                    </div>
                                </div>
                            </div>
                            <div className="col-md-3">
                                <div className="row">
                                    <div className="col-auto">
                                        <label htmlFor="code_size_limit" className="col-form-label">Code Size Limit</label>
                                    </div>
                                    <div className="col">
                                        <div className="input-group">
                                            <input type="number" className="form-control" id="code_size_limit" defaultValue={this.state.code_size_limit} onChange={(e) => { this.setState({ code_size_limit: e.target.value }); }} min={1} max={16} />
                                            <span className="input-group-text">KB</span>
                                        </div>
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
                                    Word Count: {this.state.p_description.length} / 10000
                                </div>
                                <div className="form-check form-switch">
                                    <input className="form-check-input" type="checkbox" role="switch" id="showPreview" onChange={(e) => { this.setState({ show_preview: !this.state.show_preview }) }} />
                                    <label className="form-check-label" htmlFor="showPreview">Preview</label>
                                </div>
                            </div>
                            {this.handleEditorRender()}
                        </div>

                        {/* judge code fragment for function question */}
                        {this.handleJudgeCodeEditorRender()}

                        {/* test case manage */}
                        {this.handleTestCaseRender()}
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
                                        Create a Programming Problem
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
                                        Create a Programming Problem
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
    <ProgrammingCreate
        {...props}
        navigate={useNavigate()}
    />
);