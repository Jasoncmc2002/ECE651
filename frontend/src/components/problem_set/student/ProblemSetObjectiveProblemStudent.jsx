import React, { Component } from 'react';
import { connect } from 'react-redux';
import { Link, useNavigate, useParams } from 'react-router-dom';
import ContendCard from '../../contents/ContentCard';

import Markdown from 'react-markdown';
import rehypeHighlight from 'rehype-highlight';  // highlight code, this will only add class name to code keyword span, you also need a css to control the style
import remarkGfm from 'remark-gfm';  // render table
import rehypeKatex from 'rehype-katex';  // math
import remarkMath from 'remark-math';  // math
import 'katex/dist/katex.min.css';   // math
import 'github-markdown-css';  // github markdown css to control the rendered markdown. Make sure the div enclosing the Markdown has class name: className='markdown-body'
import '../../../css/github_highlight.css';  // github syntax highlight theme
// import rehypeRaw from 'rehype-raw';  // html, package not installed
// import rehypeSanitize from 'rehype-sanitize';  // html security, package not installed
import $ from 'jquery';
import BACKEND_ADDRESS_URL from '../../config/BackendAddressURLConfig';
import GET_INFO_TIMEOUT from '../../config/GetInfoTimeoutConfig';
import { Stopwatch } from 'react-bootstrap-icons';
import Countdown from 'react-countdown';
import { Toast, ToastContainer } from 'react-bootstrap';

class ProblemSetObjectiveProblemStudent extends Component {
    state = {
        op_description: '',
        op_total_score: '--',
        opa_actual_score: '--',
        op_correct_answer: '--',

        opa_actual_answer: '',

        ps_status: '',
        // 关于题目集状态信号的约定：
        // 1. (has) not_started: 题目集未开始作答，此时不请求题目集题目信息，不能查看单个题目信息，不能作答，不显示实际得分
        // 2. (has) started: 题目集已开始作答，此时请求题目集题目信息，可以查看单个题目信息，可以作答，不显示实际得分
        // 3. (has) ended: 题目集作答时间结束，此时请求题目集题目信息，可以查看单个题目信息，不能作答，不显示实际得分
        // 4. (has been) closed: 题目集已结束，此时请求题目集题目信息，可以查看单个题目信息，不能作答，显示实际得分

        opa_change: false,

        error_message: '',

        is_loading: false,

        ps_status_enum: 1, // 根据后端返回ps_status生成

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

        show_toast: false,

        first_start_time: '',
        ps_end_time: '',
        duration: '',
    }

    componentDidMount = () => {
        if (this.props.is_login) {
            this.handleGetOneObjectiveProblem();
        } else {
            setTimeout(this.handleGetOneObjectiveProblem, GET_INFO_TIMEOUT);
        }
    }

    componentDidUpdate = (prevProps, prevState) => {
        // console.log(prevProps);
        // console.log(this.props);
        // 利用这种方式可以监控一个值是否发生变化，然后采取行动
        if (prevProps.params !== this.props.params) {
            this.handleGetOneObjectiveProblem();
        }
        // if (prevProps.is_login !== this.props.is_login && this.props.is_login) {
        //     this.handleGetOneObjectiveProblem();
        // }
    }

    handleGetOneObjectiveProblem = () => {
        this.setState({
            is_loading: true,
            error_message: ''
        });
        const token = this.props.token;
        $.ajax({
            url: BACKEND_ADDRESS_URL + "/problem_set/objective_problem/one/",
            type: "GET",
            data: {
                problemSetId: this.props.params.problem_set_id,
                objectiveProblemId: this.props.params.objective_problem_id,
            },
            headers: {
                Authorization: "Bearer " + token
            },
            success: (resp) => {
                // console.log(resp);
                if (resp.error_message === 'success') {
                    this.setState({
                        op_description: resp.op_description,
                        op_total_score: resp.op_total_score,
                        opa_actual_score: resp.opa_actual_score,
                        op_correct_answer: resp.op_correct_answer,

                        opa_actual_answer: resp.opa_actual_answer,
                        opa_change: false,

                        ps_status: resp.ps_status,

                        first_start_time: resp.first_start_time,
                        duration: resp.duration,
                        ps_end_time: resp.ps_end_time,

                        is_loading: false,
                    });
                    if (resp.ps_status === "not_started") {
                        this.setState({
                            ps_status_enum: 1,
                            show_toast: false,
                        });
                    } else if (resp.ps_status === "started") {
                        this.setState({
                            ps_status_enum: 2,
                        });
                    } else if (resp.ps_status === "ended") {
                        this.setState({
                            ps_status_enum: 3,
                            show_toast: false,
                        });
                    } else if (resp.ps_status === "closed") {
                        this.setState({
                            ps_status_enum: 4,
                            show_toast: false,
                        });
                    }
                    if (resp.ps_status !== "not_started") {
                        this.handleGetAllObjectiveProblem();
                        this.handleGetAllProgramming();
                    }
                } else {
                    this.setState({
                        is_loading: false,
                        error_message: resp.error_message,
                        ps_status_enum: 1,
                    });
                }
            }
        });
    }

    handleGetAllObjectiveProblem = () => {
        this.setState({
            is_loading: true,
        });
        const token = this.props.token;

        $.ajax({
            url: BACKEND_ADDRESS_URL + "/problem_set/objective_problem/all/",
            type: "GET",
            data: {
                problemSetId: this.props.params.problem_set_id
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

    handleGetAllProgramming = () => {
        this.setState({
            is_loading: true,
        });
        const token = this.props.token;

        $.ajax({
            url: BACKEND_ADDRESS_URL + "/problem_set/programming/all/",
            type: "GET",
            data: {
                problemSetId: this.props.params.problem_set_id
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

    handleSubmit = () => {
        // console.log("submit");
        // console.log(this.state.opa_actual_answer);
        this.setState({
            error_message: '',
            is_loading: true,
        });
        // 检查输入
        if (this.state.opa_actual_answer === '') {
            this.setState({
                error_message: 'My answer cannot be empty',
                is_loading: false,
            });
        } else if (this.state.opa_actual_answer.length > 1024) {
            this.setState({
                error_message: 'My answer cannot exceed 1,024 characters, current length: ' + this.state.opa_actual_answer.length,
                is_loading: false,
            });
        } else {
            // 联网提交
            const token = this.props.token;
            // console.log(token);
            $.ajax({
                url: BACKEND_ADDRESS_URL + "/problem_set/objective_problem/submit/",
                type: "POST",
                data: {
                    problemSetId: this.props.params.problem_set_id,
                    objectiveProblemId: this.props.params.objective_problem_id,
                    opaActualAnswer: this.state.opa_actual_answer,
                },
                headers: {
                    Authorization: "Bearer " + token
                },
                success: (resp) => {
                    if (resp.error_message === "success") {
                        this.setState({
                            error_message: "My answer submitted successfully",
                            is_loading: false,
                            opa_change: false,
                        });
                        this.handleGetOneObjectiveProblem();
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

    handleNavigationLinkRender = () => {
        // 根据本题的id，确定上一题和下一题的id
        const objective_problem_id = this.props.params.objective_problem_id;
        let index;
        for (let i = 0; i < this.state.ps_objective_problem_list.length; i++) {
            const objective_problem_i = this.state.ps_objective_problem_list[i];
            if (objective_problem_i.objective_problem_id === objective_problem_id) {
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
        let objective_problem_prev, objective_problem_prev_disabled;
        if (isNaN(index_prev) || index_prev < 0) {
            objective_problem_prev = "#";
            objective_problem_prev_disabled = true;
        } else {
            objective_problem_prev = `/problem_set/student_view/objective_problem/${this.props.params.problem_set_id}/${this.state.ps_objective_problem_list[index_prev].objective_problem_id}/`
            objective_problem_prev_disabled = false;
        }

        let objective_problem_next, objective_problem_next_disabled;
        if (isNaN(index_next) || index_next >= this.state.ps_objective_problem_list.length) {
            objective_problem_next = "#";
            objective_problem_next_disabled = true;
        } else {
            objective_problem_next = `/problem_set/student_view/objective_problem/${this.props.params.problem_set_id}/${this.state.ps_objective_problem_list[index_next].objective_problem_id}/`
            objective_problem_next_disabled = false;
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
                        disabled={objective_problem_prev_disabled}
                        onClick={() => {
                            this.props.navigate(objective_problem_prev);
                        }}>
                        Previous
                    </button>
                </div>
                <div className="col-auto">
                    <button className='btn btn-link' style={link_style} onClick={() => this.props.navigate(`/problem_set/student_view/one/${this.props.params.problem_set_id}/`)}>My Answer Sheet</button>
                </div>
                <div className="col-auto">
                    <button
                        className='btn btn-link'
                        style={link_style}
                        disabled={objective_problem_next_disabled}
                        onClick={() => {
                            this.props.navigate(objective_problem_next);
                        }}>
                        Next
                    </button>
                </div>
            </div>
        );
    }

    renderCountDownButton = () => {
        const show_str = this.state.show_toast ? "Hide" : "Show";
        if (this.state.ps_status_enum === 2) {
            return (
                <React.Fragment>
                    <button type="button" className="btn btn-outline-secondary" id="liveToastBtn" onClick={() => { this.setState({ show_toast: !this.state.show_toast }) }}>{show_str} Timer</button>
                </React.Fragment>
            );
        }
    }

    renderCountDown = () => {
        const CompleteMessage = () => <span>the answering time has expired</span>;
        const addMinutes = (date, minutes) => {
            const dateCopy = new Date(date);
            dateCopy.setMinutes(date.getMinutes() + minutes);
            return dateCopy;
        }
        if (this.state.ps_status_enum === 3) {
            return (
                <span>the answering time has expired</span>
            );
        } else if (this.state.ps_status_enum === 4) {
            return (
                <span>the problem set has ended</span>
            );
        } else if (this.state.ps_status_enum === 2) {
            // show count down
            const duration = parseInt(this.state.duration);
            if (duration === 0) {
                // 作业
                const end_date = new Date(this.state.ps_end_time);
                return (
                    <Countdown date={end_date}>
                        <CompleteMessage />
                    </Countdown>
                );
            } else if (this.state.first_start_time && this.state.first_start_time !== '') {
                const ps_end_time = new Date(this.state.ps_end_time);
                const first_start_time = new Date(this.state.first_start_time);
                const has_ended_time = addMinutes(first_start_time, duration);
                const end_date = ps_end_time < has_ended_time ? ps_end_time : has_ended_time;
                return (
                    <Countdown date={end_date}>
                        <CompleteMessage />
                    </Countdown>
                );
            }
        }
    }

    handleObjectiveProblemRender = () => {
        const objective_problem_id = this.props.params.objective_problem_id;
        let index;
        for (let i = 0; i < this.state.ps_objective_problem_list.length; i++) {
            const objective_problem_i = this.state.ps_objective_problem_list[i];
            if (objective_problem_i.objective_problem_id === objective_problem_id) {
                index = i;
                break;
            }
        }

        // const height = "40vh";
        // const op_description_style = { overflowY: "auto", maxHeight: height, minHeight: height };
        return (
            <React.Fragment>
                <div className="row mt-2">
                    <div className="col d-flex align-items-center">
                        <div className="row">
                            <div className="col-auto d-flex align-items-center">
                                <h4 className='mb-0'>
                                    Objective Problem {index + 1}
                                </h4>
                            </div>
                            <div className="col-auto d-flex align-items-center">
                                {this.handleTitleLoadingRender()}
                                {/* <span className="spinner-border" aria-hidden="true"></span> */}
                            </div>
                        </div>
                    </div>
                    <div className="col-auto d-flex align-items-center">
                        <span style={{ color: "red" }}>{this.state.error_message}</span>
                    </div>
                    <div className="col-auto d-flex align-items-center">
                        {this.renderCountDownButton()}
                    </div>
                    <div className="col-auto">
                        <div className="container-fluid">
                            <div className='card card-body'>
                                {/* <h5>作答状态</h5> */}
                                <div className="row">
                                    <div className="col-auto" style={{ fontWeight: 'bold' }}>Name: {this.props.name}</div>
                                    <div className="col-auto" style={{ fontWeight: 'bold' }}>Score: {this.state.opa_actual_score} / {this.state.op_total_score}</div>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>

                {/* op_description */}
                <div className='markdown-body'>
                    <Markdown
                        remarkPlugins={[remarkGfm, remarkMath]}
                        rehypePlugins={[rehypeHighlight, rehypeKatex]}
                    >
                        {this.state.op_description}
                    </Markdown>
                </div>
            </React.Fragment>
        );
    }

    handleTitleLoadingRender = () => {
        if (this.state.is_loading) {
            return (
                <span className="spinner-border" aria-hidden="true"></span>
            );
        }
    }

    handleButtonLoadingRender = () => {
        if (this.state.is_loading) {
            return (
                <span className="spinner-border spinner-border-sm me-2" aria-hidden="true"></span>
            );
        }
    }

    handleObjectiveProblemAnswerRender = () => {
        if (this.state.ps_status_enum === 2) {
            return (
                <div className="row mt-2">
                    <div className="col">
                        <div className="row">
                            <div className="col-auto">
                                <label htmlFor="opaActualAnswer" className="col-form-label">My Answer</label>
                            </div>
                            <div className="col">
                                <input type="text" id="opaActualAnswer" className='form-control' value={this.state.opa_actual_answer} onChange={(e) => { this.setState({ opa_actual_answer: e.target.value, opa_change: true }) }} />
                            </div>
                        </div>
                    </div>
                    <div className="col-auto d-flex justify-content-center">
                        <button className='btn btn-success' disabled={this.state.is_loading || !this.state.opa_change} onClick={() => this.handleSubmit()}>{this.handleButtonLoadingRender()}Submit</button>
                    </div>
                </div>
            );
        } else if (this.state.ps_status_enum === 3) {
            return (
                <div className="row mt-2">
                    <div className="col">
                        <div className="row">
                            <div className="col-auto">
                                <label htmlFor="opaActualAnswer" className="col-form-label">My Answer</label>
                            </div>
                            <div className="col">
                                <input type="text" id="opaActualAnswer" className='form-control' value={this.state.opa_actual_answer} disabled />
                            </div>
                        </div>
                    </div>
                    <div className="col-auto d-flex justify-content-center">
                        <button className='btn btn-success' disabled>Answering Time Expired</button>
                    </div>
                </div>
            );
        } else if (this.state.ps_status_enum === 4) {
            return (
                <div className="row mt-2">
                    <div className="col-md-6">
                        <div className="row">
                            <div className="col-auto">
                                <label htmlFor="opaActualAnswer" className="col-form-label">My Answer</label>
                            </div>
                            <div className="col">
                                <input type="text" id="opaActualAnswer" className='form-control' value={this.state.opa_actual_answer} disabled />
                            </div>
                        </div>
                    </div>
                    <div className="col-md-6">
                        <div className="row">
                            <div className="col-auto">
                                <label htmlFor="opCorrectAnswer" className="col-form-label">Correct Answer</label>
                            </div>
                            <div className="col">
                                <input type="text" id="opCorrectAnswer" className='form-control' value={this.state.op_correct_answer} disabled />
                            </div>
                        </div>
                    </div>
                </div>
            );
        }
    }

    handleProblemTablesRender = () => {
        if (this.state.ps_status_enum !== 1) {
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
                                                    <Link className='link-primary' to={`/problem_set/student_view/objective_problem/${this.props.params.problem_set_id}/${objective_problem.objective_problem_id}/`}>
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
                                                    <Link className='link-primary' to={`/problem_set/student_view/programming/${this.props.params.problem_set_id}/${programming.programming_id}/`}>
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
        } else {
            return (
                <div className="text-center">
                    <strong>Please click on Start Answering and then view the problem list</strong>
                </div>
            );
        }
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

    handleAccountRender = () => {
        if (this.props.is_login) {
            return (
                <div className="container">
                    <ContendCard>
                        {this.handleNavigationLinkRender()}
                        <hr className='mt-2' />
                        {this.handleProblemAccordionRender()}
                        {this.handleObjectiveProblemRender()}
                        {this.handleObjectiveProblemAnswerRender()}
                    </ContendCard>

                    <ToastContainer className='p-3' position='bottom-end' style={{ zIndex: 1 }}>
                        <Toast show={this.state.show_toast} onClose={() => { this.setState({ show_toast: false }) }}>
                            <Toast.Header>
                                <Stopwatch className="rounded me-2" color='rgb(13, 110, 253)' />
                                <strong className="me-auto">Timer</strong>
                                <small></small>
                            </Toast.Header>
                            <Toast.Body>
                                Time Remaining: {this.renderCountDown()}
                            </Toast.Body>
                        </Toast>
                    </ToastContainer>
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
                                        My Problem Set ID={this.props.params.problem_set_id}
                                    </h1>
                                    <h1 className="text-center">
                                        Objective Problem ID={this.props.params.objective_problem_id}
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
        <ProblemSetObjectiveProblemStudent
            {...props}
            params={useParams()}
            navigate={useNavigate()}
        />
    )
);