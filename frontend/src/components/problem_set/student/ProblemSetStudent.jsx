import React, { Component } from 'react';
import { connect } from 'react-redux';
import { Link, useParams } from 'react-router-dom';
import ContendCard from '../../contents/ContentCard';
import GET_INFO_TIMEOUT from '../../config/GetInfoTimeoutConfig';
import $ from 'jquery';
import BACKEND_ADDRESS_URL from '../../config/BackendAddressURLConfig';

import Countdown from 'react-countdown';

class ProblemSetStudent extends Component {
    state = {
        ps_name: '',
        ps_start_time: '',
        ps_end_time: '',
        duration: '',
        ps_author_name: '',

        ps_total_score: '',
        ps_actual_score: '',

        first_start_time: '',
        ps_status: '',
        // 关于题目集状态信号的约定：
        // 1. (has) not_started: 题目集未开始作答，此时不请求题目集题目信息，不能查看单个题目信息，不能作答，不显示实际得分
        // 2. (has) started: 题目集已开始作答，此时请求题目集题目信息，可以查看单个题目信息，可以作答，不显示实际得分
        // 3. (has) ended: 题目集作答时间结束，此时请求题目集题目信息，可以查看单个题目信息，不能作答，不显示实际得分
        // 4. (has been) closed: 题目集已结束，此时请求题目集题目信息，可以查看单个题目信息，不能作答，显示实际得分

        error_message: '',
        ps_status_message: '',  // 根据后端返回ps_status生成

        is_loading: false,
        ps_status_enum: '',  // 根据后端返回ps_status生成
        is_exam: true,  // 根据后端返回duration生成

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
        if (this.props.is_login) {
            this.handleGetOne();
        } else {
            setTimeout(this.handleGetOne, GET_INFO_TIMEOUT);
        }
    }

    handleStart = () => {
        this.setState({
            is_loading: true,
            error_message: ''
        });
        // console.log("start");
        const token = this.props.token;
        // console.log(token);
        $.ajax({
            url: BACKEND_ADDRESS_URL + "/problem_set/start/",
            type: "PUT",
            data: {
                problemSetId: this.props.params.problem_set_id,
            },
            headers: {
                Authorization: "Bearer " + token
            },
            success: (resp) => {
                if (resp.error_message === "success") {
                    this.setState({
                        is_loading: false,
                    });
                    this.handleGetOne();
                } else {
                    this.setState({
                        is_loading: false,
                        error_message: resp.error_message
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

    handleGetOne = () => {
        this.setState({
            is_loading: true,
            error_message: ''
        });
        // console.log("get one");
        const token = this.props.token;
        // console.log(token);
        $.ajax({
            url: BACKEND_ADDRESS_URL + "/problem_set/",
            type: "GET",
            data: {
                problemSetId: this.props.params.problem_set_id,
            },
            headers: {
                Authorization: "Bearer " + token
            },
            success: (resp) => {
                // console.log(resp);
                if (resp.error_message === "success") {
                    this.setState({
                        ps_name: resp.ps_name,
                        ps_start_time: resp.ps_start_time,
                        ps_end_time: resp.ps_end_time,
                        duration: resp.duration,
                        ps_author_name: resp.ps_author_name,

                        ps_total_score: resp.ps_total_score,
                        ps_actual_score: resp.ps_actual_score,

                        first_start_time: resp.first_start_time,
                        ps_status: resp.ps_status,
                        is_loading: false,
                    });

                    // 根据duration判断是否为考试
                    if (parseInt(resp.duration) === 0) {
                        this.setState({
                            is_exam: false,
                        });
                    } else {
                        this.setState({
                            is_exam: true,
                        });
                    }
                    // 根据ps_status判断其他变量
                    // started, not_started, timeout, ended,
                    if (resp.ps_status === "not_started") {
                        this.setState({
                            ps_status_message: 'The answering has not started',
                            ps_status_enum: 1,
                        });
                    } else if (resp.ps_status === "started") {
                        this.setState({
                            ps_status_message: 'The answering has started',
                            ps_status_enum: 2,
                        });
                    } else if (resp.ps_status === "ended") {
                        this.setState({
                            ps_status_message: 'The answering time has expired',
                            ps_status_enum: 3,
                        });
                    } else if (resp.ps_status === "closed") {
                        this.setState({
                            ps_status_message: 'The problem set has ended',
                            ps_status_enum: 4,
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

    handleStartModalRender = () => {
        if (this.state.is_exam) {
            return (
                <React.Fragment>
                    {/* 点击开始考试modal */}
                    {/* <!-- Button trigger modal --> */}
                    <button type="button" className="btn btn-outline-primary btn-sm mt-2" data-bs-toggle="modal" data-bs-target="#startExam">
                        Start Answering
                    </button>

                    {/* <!-- Modal --> */}
                    <div className="modal fade" id="startExam" tabIndex="-1" aria-labelledby="startExamLabel" aria-hidden="true">
                        <div className="modal-dialog modal-dialog-centered">
                            <div className="modal-content">
                                <div className="modal-header">
                                    <h1 className="modal-title fs-5" id="startExamLabel">Confirm Start Answering</h1>
                                    <button type="button" className="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                                </div>
                                <div className="modal-body">
                                    <strong>The exam duration is {this.state.duration} minutes</strong>. The timer cannot be paused after the answering starts. You will not be able to answer questions after the exam time is up. Confirm to start the answering?
                                </div>
                                <div className="modal-footer">
                                    <div className='' style={{ color: "red" }}>
                                        {this.state.error_message}
                                    </div>
                                    <button type="button" className="btn btn-secondary" data-bs-dismiss="modal">Close</button>
                                    <button type="button" className="btn btn-primary" disabled={this.state.ps_status_enum !== 1} onClick={() => this.handleStart()} data-bs-dismiss="modal">Confirm</button>
                                </div>
                            </div>
                        </div>
                    </div>
                </React.Fragment>
            );
        } else {
            return (
                <button type="button" className="btn btn-outline-primary btn-sm mt-2" disabled={this.state.ps_status_enum !== 1} onClick={() => this.handleStart()}>
                    Start Answering
                </button>
            );
        }
    }

    renderCountDown = () => {
        const CompleteMessage = () => <span>The answering time has expired</span>;
        const addMinutes = (date, minutes) => {
            const dateCopy = new Date(date);
            dateCopy.setMinutes(date.getMinutes() + minutes);
            return dateCopy;
        }
        if (this.state.ps_status_enum === 3) {
            return (
                <span>The answering time has expired</span>
            );
        } else if (this.state.ps_status_enum === 4) {
            return (
                <span>The problem set has ended</span>
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
                const answering_expired_time = addMinutes(first_start_time, duration);
                const end_date = ps_end_time < answering_expired_time ? ps_end_time : answering_expired_time;
                return (
                    <Countdown date={end_date}>
                        <CompleteMessage />
                    </Countdown>
                );
            }
        }
    }

    handleProblemSetStatusRender = () => {
        let first_start_time_str = '';
        if (this.state.first_start_time === '') {
            first_start_time_str = "N/A"
        } else {
            const first_start_time = new Date(this.state.first_start_time);
            first_start_time_str = first_start_time.toLocaleString('zh-CN');
        }
        if (this.state.ps_status_enum !== 1) {

            return (
                <div className="container-fluid">
                    <ContendCard>
                        <h5>Answering Status</h5>
                        <div>Problem Set Status: {this.state.ps_status_message}</div>
                        <div>Answering Start Time: {first_start_time_str}</div>
                        <div>Time Remaining: {this.renderCountDown()}</div>
                        <div style={{ height: '24px' }}></div>
                    </ContendCard>
                </div>
            );
        } else {
            return (
                <div className="container-fluid">
                    <ContendCard>
                        <h5>Answering Status</h5>
                        <div>Problem Set Status: {this.state.ps_status_message}</div>

                        {this.handleStartModalRender()}
                        <div style={{ height: '33px' }}></div>
                    </ContendCard>
                </div>
            );
        }
    }

    handleProblemSetInfoRender = () => {
        const ps_start_time = new Date(this.state.ps_start_time);
        const ps_end_time = new Date(this.state.ps_end_time);
        if (this.state.is_exam) {
            return (
                <div className="container-fluid">
                    <ContendCard>
                        <h5>Problem Set Information</h5>
                        <div>Author: {this.state.ps_author_name}</div>
                        <div>Start Time: {ps_start_time.toLocaleString('zh-CN')}</div>
                        <div>End Time: {ps_end_time.toLocaleString('zh-CN')}</div>
                        <div>Duration: {this.state.duration} minutes</div>
                    </ContendCard>
                </div>
            );
        } else {
            return (
                <div className="container-fluid">
                    <ContendCard>
                        <h5>Problem Set Information</h5>
                        <div>Author: {this.state.ps_author_name}</div>
                        <div>Start Time: {ps_start_time.toLocaleString('zh-CN')}</div>
                        <div>End Time: {ps_end_time.toLocaleString('zh-CN')}</div>
                        <div style={{ height: '24px' }}></div>
                    </ContendCard>
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
                    <strong>Please click on Start Answering and then view my answer sheet</strong>
                </div>
            );
        }
    }

    handleLoadingRender = () => {
        if (this.state.is_loading) {
            return (
                <span className="spinner-border ms-2" aria-hidden="true"></span>
            );
        }
    }

    handleAccountRender = () => {
        if (this.props.is_login) {
            return (
                <div className="container">
                    <ContendCard>
                        <div className="ps-name row">
                            <div className="col">
                                <h4 className='text-center'>
                                    {this.state.ps_name}
                                </h4>
                            </div>
                            <div className="col-auto text-end d-flex align-items-center">
                                <span style={{ color: "red" }}>{this.state.error_message}</span>
                                {this.handleLoadingRender()}
                            </div>
                        </div>

                        <hr className='m-0' />

                        <div className="container-fluid-drop">
                            <div className="row g-0">
                                <div className="col-md-6">
                                    {this.handleProblemSetInfoRender()}
                                </div>
                                <div className="col-md-6">
                                    {this.handleProblemSetStatusRender()}
                                </div>
                            </div>
                        </div>

                        <div className="container-fluid">
                            <ContendCard>
                                <h5>My Answer Sheet</h5>
                                <div className="d-flex justify-content-around">
                                    <div className="text-center">
                                        <strong>Student Name: {this.props.name}</strong>
                                    </div>
                                    <div className="text-center">
                                        <strong>Final Score: {this.state.ps_actual_score} / {this.state.ps_total_score}</strong>
                                    </div>
                                </div>
                                <hr />
                                {this.handleProblemTablesRender()}
                            </ContendCard>
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
                                        View My Problem Set ID={this.props.params.problem_set_id}
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
        <ProblemSetStudent
            {...props}
            params={useParams()}
        />
    )
);