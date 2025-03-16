import React, { Component } from 'react';
import { connect } from 'react-redux';
import { useNavigate, useParams } from 'react-router-dom';
import ContendCard from '../../contents/ContentCard';
import { Link } from 'react-router-dom';

import Markdown from 'react-markdown';
import rehypeHighlight from 'rehype-highlight';  // highlight code, this will only add class name to code keyword span, you also need a css to control the style
import remarkGfm from 'remark-gfm';  // render table
import rehypeKatex from 'rehype-katex';  // math
import remarkMath from 'remark-math';  // math
import 'katex/dist/katex.min.css';   // math
import 'github-markdown-css';  // github markdown css to control the rendered markdown. Make sure the div enclosing the Markdown has class name: className='markdown-body'
import '../../../css/github_highlight.css';  // github syntax highlight theme

import CodeMirror from '@uiw/react-codemirror';
import { python } from '@codemirror/lang-python';

import $ from 'jquery';
import BACKEND_ADDRESS_URL from '../../config/BackendAddressURLConfig';
import GET_INFO_TIMEOUT from '../../config/GetInfoTimeoutConfig';

class ProblemSetProgrammingTeacher extends Component {
    state = {
        p_title: '',
        p_description: '',
        p_total_score: '--',
        time_limit: '--',
        code_size_limit: '--',

        student_name: '',
        student_username: '',

        pa_code: '',
        pa_actual_score: '--',
        pass_count: '--',
        tc_count: '--',

        error_message: '',

        is_loading: false,

        ps_objective_problem_list: [
            // { objective_problem_id: '1', op_description: 'Question 1', op_total_score: '10', opa_actual_score: '--', opa_status: '已作答' },
            // { objective_problem_id: '2', op_description: 'Question 2', op_total_score: '8', opa_actual_score: '--', opa_status: '已作答' },
            // { objective_problem_id: '3', op_description: 'Question 3', op_total_score: '5', opa_actual_score: '--', opa_status: '未作答' },
        ],
        ps_programming_list: [
            // { programming_id: '1', p_title: 'Programming 1', p_total_score: '25', pa_actual_score: '20', pa_status: '已作答' },
            // { programming_id: '2', p_title: 'Programming 2', p_total_score: '20', pa_actual_score: '20', pa_status: '已作答' },
            // { programming_id: '3', p_title: 'Programming 3', p_total_score: '15', pa_actual_score: '--', pa_status: '未作答' },
        ],
    }

    componentDidMount = () => {
        // console.log(this.props.params);
        if (this.props.is_login) {
            this.handleGetOneStudentOneProgramming();
        } else {
            setTimeout(this.handleGetOneStudentOneProgramming, GET_INFO_TIMEOUT);
        }
    }

    componentDidUpdate = (prevProps, prevState) => {
        // console.log(prevProps);
        // console.log(this.props);
        // 利用这种方式可以监控一个值是否发生变化，然后采取行动
        if (prevProps.params !== this.props.params) {
            this.handleGetOneStudentOneProgramming();
        }
    }

    handleGetOneStudentOneProgramming = () => {
        this.setState({
            is_loading: true,
            error_message: ''
        });
        const token = this.props.token;
        // console.log(token);

        $.ajax({
            url: BACKEND_ADDRESS_URL + "/problem_set/teacher/one_student_one_programming/",
            type: "GET",
            data: {
                problemSetId: this.props.params.problem_set_id,
                programmingId: this.props.params.programming_id,
                studentId: this.props.params.student_id,
            },
            headers: {
                Authorization: "Bearer " + token
            },
            success: (resp) => {
                // console.log(resp);
                if (resp.error_message === 'success') {
                    this.setState({
                        p_title: resp.p_title,
                        p_description: resp.p_description,
                        p_total_score: resp.p_total_score,
                        time_limit: resp.time_limit,
                        code_size_limit: resp.code_size_limit,

                        student_name: resp.student_name,
                        student_username: resp.student_username,

                        pa_code: resp.pa_code,
                        pa_actual_score: resp.pa_actual_score,
                        pass_count: resp.pass_count,
                        tc_count: resp.tc_count,

                        is_loading: false,
                    });

                    this.handleGetOneStudentAllObjectiveProblem();
                    this.handleGetOneStudentAllProgramming();
                } else {
                    this.setState({
                        is_loading: false,
                        error_message: resp.error_message,
                    });
                }
            }
        });
    }

    handleGetOneStudentAllObjectiveProblem = () => {
        this.setState({
            is_loading: true
        });
        const token = this.props.token;

        $.ajax({
            url: BACKEND_ADDRESS_URL + "/problem_set/teacher/one_student_all_objective_problem/",
            type: "GET",
            data: {
                problemSetId: this.props.params.problem_set_id,
                studentId: this.props.params.student_id,
            },
            headers: {
                Authorization: "Bearer " + token
            },
            success: (resp) => {
                // console.log(resp);
                this.setState({
                    ps_objective_problem_list: resp,
                    is_loading: false,
                });
                if (resp.error_message) {
                    console.log(resp.error_message);
                }
            }
        });
    }

    handleGetOneStudentAllProgramming = () => {
        this.setState({
            is_loading: true
        });
        const token = this.props.token;

        $.ajax({
            url: BACKEND_ADDRESS_URL + "/problem_set/teacher/one_student_all_programming/",
            type: "GET",
            data: {
                problemSetId: this.props.params.problem_set_id,
                studentId: this.props.params.student_id,
            },
            headers: {
                Authorization: "Bearer " + token
            },
            success: (resp) => {
                // console.log(resp);
                this.setState({
                    ps_programming_list: resp,
                    is_loading: false,
                });
                if (resp.error_message) {
                    console.log(resp.error_message);
                }
            }
        });
    }

    handleNavigationLinkRender = () => {
        // 根据本题的id，确定上一题和下一题的id
        const programming_id = this.props.params.programming_id;
        let index;
        for (let i = 0; i < this.state.ps_programming_list.length; i++) {
            const programming_i = this.state.ps_programming_list[i];
            if (programming_i.programming_id === programming_id) {
                index = i;
                break;
            }
        }
        // console.log(index);
        const index_prev = index - 1;
        const index_next = index + 1;
        // console.log(index_next);
        // console.log(index_prev);
        // 生成链接地址
        let programming_prev, programming_prev_disabled;
        if (isNaN(index_prev) || index_prev < 0) {
            programming_prev = "#";
            programming_prev_disabled = true;
        } else {
            programming_prev = `/problem_set/teacher_view/programming/${this.props.params.problem_set_id}/${this.state.ps_programming_list[index_prev].programming_id}/${this.props.params.student_id}/`
            programming_prev_disabled = false;
        }

        let programming_next, programming_next_disabled;
        if (isNaN(index_next) || index_next >= this.state.ps_programming_list.length) {
            programming_next = "#";
            programming_next_disabled = true;
        } else {
            programming_next = `/problem_set/teacher_view/programming/${this.props.params.problem_set_id}/${this.state.ps_programming_list[index_next].programming_id}/${this.props.params.student_id}/`
            programming_next_disabled = false;
        }

        const link_style = {
            textDecoration: "none",
            fontWeight: "bold"
        };
        return (
            <div className="row justify-content-between">
                <div className="col-auto">
                    <button
                        className='btn btn-link'
                        style={link_style}
                        disabled={programming_prev_disabled}
                        onClick={() => {
                            this.props.navigate(programming_prev);
                        }}>
                        Previous
                    </button>
                </div>
                <div className="col-auto">
                    <button className='btn btn-link' style={link_style} onClick={() => this.props.navigate(`/problem_set/teacher_view/one_record/${this.props.params.problem_set_id}/${this.props.params.student_id}/`)}>{this.state.student_name}'s Answer Sheet</button>
                </div>
                <div className="col-auto">
                    <button
                        className='btn btn-link'
                        style={link_style}
                        disabled={programming_next_disabled}
                        onClick={() => {
                            this.props.navigate(programming_next);
                        }}>
                        Next
                    </button>
                </div>
            </div>
        );
    }

    handleProblemAccordionRender = () => {
        return (
            <div className="accordion mt-2" id="problemAccordion">
                <div className="accordion-item">
                    <h2 className="accordion-header">
                        <button className="accordion-button collapsed" type="button" data-bs-toggle="collapse" data-bs-target="#panelsStayOpen-collapseOne" aria-expanded="false" aria-controls="panelsStayOpen-collapseOne">
                            Problem List
                        </button>
                    </h2>
                    <div id="panelsStayOpen-collapseOne" className="accordion-collapse collapse">
                        <div className="accordion-body">
                            {this.handleProblemTablesRender()}
                        </div>
                    </div>
                </div>
            </div>
        );
    }

    handleProblemTablesRender = () => {
        return (
            <div className="container-fluid mt-2">
                <div className="row">
                    <div className="col-md-6">
                        <h6>
                            Objective Problems: {this.state.ps_objective_problem_list.length}
                        </h6>

                        <table className="table table-hover">
                            <thead>
                                <tr>
                                    <th scope="col">#</th>
                                    <th scope="col">Description</th>
                                    <th scope="col">Score</th>
                                    <th scope="col">Status</th>
                                </tr>
                            </thead>
                            <tbody>
                                {this.state.ps_objective_problem_list.map((objective_problem) => {
                                    return (
                                        <tr key={'op' + objective_problem.objective_problem_id}>
                                            <th scope="row">
                                                {this.state.ps_objective_problem_list.indexOf(objective_problem) + 1}
                                            </th>
                                            <td style={{ overflow: "hidden", textOverflow: "ellipsis", maxWidth: "15vw", minWidth: "15vw", textWrap: "nowrap" }}>
                                                <Link className='link-primary' to={`/problem_set/teacher_view/objective_problem/${this.props.params.problem_set_id}/${objective_problem.objective_problem_id}/${this.props.params.student_id}/`}>
                                                    {objective_problem.op_description}
                                                </Link>
                                            </td>
                                            <td>{objective_problem.opa_actual_score} / {objective_problem.op_total_score}</td>
                                            <td>{objective_problem.opa_status}</td>
                                        </tr>
                                    );
                                })}
                            </tbody>
                        </table>
                    </div>
                    <div className="col-md-6">
                        <h6>
                            Programming Problems: {this.state.ps_programming_list.length}
                        </h6>

                        <table className="table table-hover">
                            <thead>
                                <tr>
                                    <th scope="col">#</th>
                                    <th scope="col">Title</th>
                                    <th scope="col">Score</th>
                                    <th scope="col">Status</th>
                                </tr>
                            </thead>
                            <tbody>
                                {this.state.ps_programming_list.map((programming) => {
                                    return (
                                        <tr key={'p' + programming.programming_id}>
                                            <th scope="row">
                                                {this.state.ps_programming_list.indexOf(programming) + 1}
                                            </th>
                                            <td style={{ overflow: "hidden", textOverflow: "ellipsis", maxWidth: "15vw", minWidth: "15vw", textWrap: "nowrap" }}>
                                                <Link className='link-primary' to={`/problem_set/teacher_view/programming/${this.props.params.problem_set_id}/${programming.programming_id}/${this.props.params.student_id}/`}>
                                                    {programming.p_title}
                                                </Link>
                                            </td>
                                            <td>{programming.pa_actual_score} / {programming.p_total_score}</td>
                                            <td>{programming.pa_status}</td>
                                        </tr>
                                    );
                                })}
                            </tbody>
                        </table>
                    </div>
                </div>
            </div>
        )
    }

    handleTitleLoadingRender = () => {
        if (this.state.is_loading) {
            return (
                <span className="spinner-border" aria-hidden="true"></span>
            );
        }
    }

    handleProgrammingRender = () => {
        const programming_id = this.props.params.programming_id;
        let index;
        for (let i = 0; i < this.state.ps_programming_list.length; i++) {
            const programming_i = this.state.ps_programming_list[i];
            if (programming_i.programming_id === programming_id) {
                index = i;
                break;
            }
        }

        return (
            <React.Fragment>
                <div className="row mt-2">
                    <div className="col d-flex align-items-center">
                        <div className="row">
                            <div className="col-auto d-flex align-items-center">
                                <h4 className='mb-0'>
                                    Programming Problem {index + 1}
                                    {/* Programming Problem {index + 1}: {this.state.p_title} */}
                                </h4>
                            </div>
                            {/* <div className="col-auto d-flex align-items-center">
                                <div className="container-fluid card card-body p-2" style={{ fontFamily: "monospace", fontSize: "15px" }}>
                                    Time Limit: {this.state.time_limit}ms <br />
                                    Code Size Limit: {this.state.code_size_limit}KB
                                </div>
                            </div> */}
                            <div className="col-auto d-flex align-items-center">
                                {this.handleTitleLoadingRender()}
                                {/* <span className="spinner-border" aria-hidden="true"></span> */}
                            </div>
                        </div>
                    </div>
                    <div className="col-auto d-flex align-items-center">
                        <span style={{ color: "red" }}>{this.state.error_message}</span>
                    </div>
                    <div className="col-auto">
                        <div className="container-fluid">
                            <div className='card card-body'>
                                {/* <h5>作答状态</h5> */}
                                <div className="row">
                                    <div className="col-auto" style={{ fontWeight: 'bold' }}>Username: {this.state.student_username}</div>
                                    <div className="col-auto" style={{ fontWeight: 'bold' }}>Name: {this.state.student_name}</div>
                                    <div className="col-auto" style={{ fontWeight: 'bold' }}>Score: {this.state.pa_actual_score} / {this.state.p_total_score}</div>
                                    <div className="col-auto" style={{ fontWeight: 'bold' }}>Test Case Passed: {this.state.pass_count} / {this.state.tc_count}</div>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>

                <div className="row mt-2">
                    <div className="col d-flex align-items-center">
                        <div className="row">
                            <div className="col-auto d-flex align-items-center">
                                <h4 className='mb-0'>
                                    {this.state.p_title}
                                </h4>
                            </div>
                            <div className="col-auto d-flex align-items-center">
                                <div className="container-fluid card card-body p-2" style={{ fontFamily: "monospace", fontSize: "15px" }}>
                                    Time Limit: {this.state.time_limit}ms <br />
                                    Code Size Limit: {this.state.code_size_limit}KB
                                </div>
                            </div>
                        </div>
                    </div>
                </div>

                {/* op_description */}
                <div className='markdown-body mt-2'>
                    <Markdown
                        remarkPlugins={[remarkGfm, remarkMath]}
                        rehypePlugins={[rehypeHighlight, rehypeKatex]}
                    >
                        {this.state.p_description}
                    </Markdown>
                </div>
            </React.Fragment>
        );
    }

    handleProgrammingEditorRender = () => {
        return (
            <React.Fragment>
                <hr />
                <div className="mt-3" style={{ borderStyle: "solid", borderWidth: "1px", borderColor: "rgb(243, 243, 243)" }}>
                    <CodeMirror
                        value={this.state.pa_code}
                        height="50vh"
                        extensions={[python()]}
                        basicSetup={{
                            tabSize: 4
                        }}
                        editable={false}
                        readOnly={true}
                    />
                </div>
            </React.Fragment>
        );
    }

    handlePermissionRender = () => {
        if (this.props.permission > 0) {
            return (
                // real display of this page
                <div className="container">
                    <ContendCard>
                        {this.handleNavigationLinkRender()}
                        <hr className='mt-2' />
                        {this.handleProblemAccordionRender()}
                        {this.handleProgrammingRender()}
                        {this.handleProgrammingEditorRender()}
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
                                    <h1 className="text-center">Programming Problem ID={this.props.params.programming_id}</h1>
                                    <h1 className="text-center">Student ID={this.props.params.student_id}</h1>
                                    <h1 className="text-center">
                                        Problem Set ID={this.props.params.problem_set_id}
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
                                    <h1 className="text-center">Programming Problem ID={this.props.params.programming_id}</h1>
                                    <h1 className="text-center">Student ID={this.props.params.student_id}</h1>
                                    <h1 className="text-center">
                                        Problem Set ID={this.props.params.problem_set_id}
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

export default connect(mapStateToProps, null)(
    (props) => (
        <ProblemSetProgrammingTeacher
            {...props}
            params={useParams()}
            navigate={useNavigate()}
        />
    )
);