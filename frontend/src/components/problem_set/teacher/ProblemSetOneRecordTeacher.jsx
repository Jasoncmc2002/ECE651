import React, { Component } from 'react';
import { connect } from 'react-redux';
import { useParams } from 'react-router-dom';
import ContendCard from '../../contents/ContentCard';
import { Link } from 'react-router-dom';
import GET_INFO_TIMEOUT from '../../config/GetInfoTimeoutConfig';
import $ from 'jquery';
import BACKEND_ADDRESS_URL from '../../config/BackendAddressURLConfig';

class ProblemSetOneRecordTeacher extends Component {
    state = {
        ps_name: '',
        student_name: '',
        student_username: '',
        ps_start_time: '',
        ps_end_time: '',
        duration: '',
        ps_author_name: '',

        ps_total_score: '--',
        ps_actual_score: '--',

        first_start_time: '',

        error_message: '',
        ps_status_message: '',
        // 后端直接返回，只有3个状态，题目集未开始，题目集已开始，题目集已结束

        is_loading: false,
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
        // console.log(this.props.params);
        if (this.props.is_login) {
            this.handleGetOneStudentRecord();
        } else {
            setTimeout(this.handleGetOneStudentRecord, GET_INFO_TIMEOUT);
        }
    }

    handleGetOneStudentRecord = () => {
        this.setState({
            is_loading: true,
            error_message: ''
        });
        const token = this.props.token;
        // console.log(token);
        $.ajax({
            url: BACKEND_ADDRESS_URL + "/problem_set/teacher/one_student_record/",
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
                if (resp.error_message === 'success') {
                    this.setState({
                        ps_name: resp.ps_name,
                        student_name: resp.student_name,
                        student_username: resp.student_username,
                        ps_start_time: resp.ps_start_time,
                        ps_end_time: resp.ps_end_time,
                        duration: resp.duration,
                        ps_author_name: resp.ps_author_name,

                        ps_total_score: resp.ps_total_score,
                        ps_actual_score: resp.ps_actual_score,

                        first_start_time: resp.first_start_time,

                        ps_status_message: resp.ps_status_message,
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

    handleLoadingRender = () => {
        if (this.state.is_loading) {
            return (
                <span className="spinner-border ms-2" aria-hidden="true"></span>
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

    handleProblemSetStatusRender = () => {
        let first_start_time_str = '';
        if (this.state.first_start_time === '') {
            first_start_time_str = "N/A"
        } else {
            const first_start_time = new Date(this.state.first_start_time);
            first_start_time_str = first_start_time.toLocaleString('zh-CN');
        }
        return (
            <div className="container-fluid">
                <ContendCard>
                    <h5>Answering Status</h5>
                    <div>Problem Set Status: {this.state.ps_status_message}</div>
                    <div>Answering Start Time: {first_start_time_str}</div>
                    <div style={{ height: '24px' }}></div>
                    <div style={{ height: '24px' }}></div>
                </ContendCard>
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

    handlePermissionRender = () => {
        if (this.props.permission > 0) {
            return (
                // real display of this page
                <div className="container">
                    <ContendCard>
                        <div className="ps-name row">
                            <div className="col">
                                <h4 className='text-center'>
                                    {this.state.ps_name} - {this.state.student_name}'s Transcript
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
                                <h5>Student's Answer Sheet</h5>
                                <div className="d-flex justify-content-around">
                                    <div className="text-center">
                                        <strong>Student Username: {this.state.student_username}</strong>
                                    </div>
                                    <div className="text-center">
                                        <strong>Student Name: {this.state.student_name}</strong>
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
        <ProblemSetOneRecordTeacher
            {...props}
            params={useParams()}
        />
    )
);