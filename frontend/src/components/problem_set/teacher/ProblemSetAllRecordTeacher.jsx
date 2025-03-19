import React, { Component } from 'react';
import { connect } from 'react-redux';
import { useParams, useNavigate } from 'react-router-dom';
import ContendCard from '../../contents/ContentCard';
import { Link } from 'react-router-dom';
import GET_INFO_TIMEOUT from '../../config/GetInfoTimeoutConfig';
import $ from 'jquery';
import BACKEND_ADDRESS_URL from '../../config/BackendAddressURLConfig';
import * as XLSX from 'xlsx';
import { saveAs } from 'file-saver';

class ProblemSetAllRecordTeacher extends Component {
    state = {
        ps_name: '',
        ps_author_name: '',
        ps_start_time: '',
        ps_end_time: '',
        duration: '',

        ps_status_message: '',
        // 后端直接返回，只有3个状态，题目集未开始，题目集已开始，题目集已结束

        ps_total_score: '',

        ps_student_list: [
            // { user_id: '4', name: 'bb', username: '04', permission: '0', first_start_time: '2024-12-16T17:25', ps_actual_score: '25' },
            // { user_id: '5', name: 'cc', username: '05', permission: '0', first_start_time: '', ps_actual_score: '0' },
            // { user_id: '6', name: 'dd', username: '06', permission: '0', first_start_time: '2024-04-06T17:25', ps_actual_score: '74' },
            // { user_id: '7', name: 'ee', username: '06', permission: '0', first_start_time: '2024-04-06T17:25', ps_actual_score: '100' },
            // { user_id: '8', name: 'ff', username: '06', permission: '0', first_start_time: '', ps_actual_score: '0' },
        ],

        ps_objective_problem_list: [
            // { objective_problem_id: '4', op_description: 'Question 466666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666', op_correct_count: '2', op_answer_count: '6' },
            // { objective_problem_id: '5', op_description: 'Question 5', op_correct_count: '4', op_answer_count: '5' },
            // { objective_problem_id: '6', op_description: 'Question 6', op_correct_count: '0', op_answer_count: '5' },
            // { objective_problem_id: '7', op_description: 'Question 7', op_correct_count: '0', op_answer_count: '0' },
        ],

        ps_programming_list: [
            // { programming_id: '4', p_title: 'Question 4', p_correct_count: '3', p_answer_count: '7' },
            // { programming_id: '5', p_title: 'Question 5', p_correct_count: '2', p_answer_count: '6' },
            // { programming_id: '6', p_title: 'Question 6', p_correct_count: '0', p_answer_count: '6' },
            // { programming_id: '7', p_title: 'Question 7', p_correct_count: '0', p_answer_count: '0' },
        ],

        error_message: '',

        is_loading: false,
        is_exam: true,
    }

    componentDidMount = () => {
        // console.log(this.props.params);
        if (this.props.is_login) {
            this.handleGetOneProblemSetInfo();
        } else {
            setTimeout(this.handleGetOneProblemSetInfo, GET_INFO_TIMEOUT);
        }
    }

    handleGetOneProblemSetInfo = () => {
        this.setState({
            is_loading: true,
            error_message: ''
        });
        const token = this.props.token;
        // console.log(token);
        $.ajax({
            url: BACKEND_ADDRESS_URL + "/problem_set/teacher/one_problem_set_info/",
            type: "GET",
            data: {
                problemSetId: this.props.params.problem_set_id,
            },
            headers: {
                Authorization: "Bearer " + token
            },
            success: (resp) => {
                // console.log(resp);
                if (resp.error_message === 'success') {
                    this.setState({
                        ps_name: resp.ps_name,
                        ps_author_name: resp.ps_author_name,
                        ps_start_time: resp.ps_start_time,
                        ps_end_time: resp.ps_end_time,
                        duration: resp.duration,

                        ps_status_message: resp.ps_status_message,

                        ps_total_score: resp.ps_total_score,

                        is_loading: false,
                    });

                    // decide exam
                    if (parseInt(resp.duration) === 0) {
                        this.setState({
                            is_exam: false
                        });
                    } else {
                        this.setState({
                            is_exam: true
                        });
                    }

                    this.handleGetAllStudentRecord();
                    this.handleGetAllObjectiveProblemRecord();
                    this.handleGetAllProgrammingRecord();
                } else {
                    this.setState({
                        is_loading: false,
                        error_message: resp.error_message,
                    });
                }
            }
        });
    }

    handleGetAllStudentRecord = () => {
        this.setState({
            is_loading: true,
        });
        const token = this.props.token;
        // console.log(token);
        $.ajax({
            url: BACKEND_ADDRESS_URL + "/problem_set/teacher/all_student_record/",
            type: "GET",
            data: {
                problemSetId: this.props.params.problem_set_id,
            },
            headers: {
                Authorization: "Bearer " + token
            },
            success: (resp) => {
                // console.log(resp);
                this.setState({
                    ps_student_list: resp,
                    is_loading: false,
                });
                if (resp.error_message) {
                    console.log(resp);
                }
            }
        });
    }

    handleGetAllObjectiveProblemRecord = () => {
        this.setState({
            is_loading: true,
        });
        const token = this.props.token;
        // console.log(token);
        $.ajax({
            url: BACKEND_ADDRESS_URL + "/problem_set/teacher/all_objective_problem_record/",
            type: "GET",
            data: {
                problemSetId: this.props.params.problem_set_id,
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
                    console.log(resp);
                }
            }
        });
    }

    handleGetAllProgrammingRecord = () => {
        this.setState({
            is_loading: true,
        });
        const token = this.props.token;
        // console.log(token);
        $.ajax({
            url: BACKEND_ADDRESS_URL + "/problem_set/teacher/all_programming_record/",
            type: "GET",
            data: {
                problemSetId: this.props.params.problem_set_id,
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
                    console.log(resp);
                }
            }
        });
    }

    handleNavigateBack = () => {
        if (this.state.is_exam) {
            this.props.navigate(`/set_manage/exam/${this.props.params.problem_set_id}/`);
        } else {
            this.props.navigate(`/set_manage/assignment/${this.props.params.problem_set_id}/`);
        }
    }

    handleLoadingRender = () => {
        if (this.state.is_loading) {
            return (
                <span className="spinner-border ms-2" aria-hidden="true"></span>
            );
        }
    }

    handleProblemSetInfoRender = () => {
        const duration = this.state.duration === '0' ? 'N/A' : (this.state.duration + ' minutes');
        const ps_start_time = this.state.ps_start_time === '' ? '' : new Date(this.state.ps_start_time);
        const ps_end_time = this.state.ps_end_time === '' ? '' : new Date(this.state.ps_end_time);
        return (
            <div className="container-fluid">
                <ContendCard>
                    <h5>Problem Set Information</h5>
                    <div className="row">
                        <div className="col-md-6">
                            <div>Author: {this.state.ps_author_name}</div>
                            <div>Start Time: {ps_start_time.toLocaleString('zh-CN')}</div>
                            <div>End Time: {ps_end_time.toLocaleString('zh-CN')}</div>
                            <div>Duration: {duration}</div>
                        </div>
                        <div className="col-md-6">
                            <div>Problem Set Status: {this.state.ps_status_message}</div>
                            <div>Objective Problems: {this.state.ps_objective_problem_list.length}</div>
                            <div>Programming Problems: {this.state.ps_programming_list.length}</div>
                            <div>Participating Students: {this.state.ps_student_list.length}</div>
                        </div>
                    </div>
                </ContendCard>
            </div>
        )
    }

    handleAllStudentRecordExport = () => {
        // console.log("export");
        // 准备数据
        const ps_student_list_sorted = [];
        this.state.ps_student_list.forEach((student) => {
            // 转化格式
            const ps_actual_score = parseInt(student.ps_actual_score);

            // 计算开始时间
            let first_start_time_str;
            if (student.first_start_time === '') {
                first_start_time_str = 'N/A';
            } else {
                const first_start_time = new Date(student.first_start_time);
                first_start_time_str = first_start_time.toLocaleString('zh-CN');
            }

            // 生成新列表
            ps_student_list_sorted.push({
                ...student,
                first_start_time_str: first_start_time_str,
                ps_actual_score: ps_actual_score,
                permission: parseInt(student.permission)
            });
        });

        // 排序
        ps_student_list_sorted.sort((a, b) => (b.ps_actual_score - a.ps_actual_score));

        // 准备导出
        const ps_student_list_export = [];
        ps_student_list_sorted.forEach((student) => {
            const permission_str = student.permission < 1 ? "Student" : student.permission < 2 ? "Teacher" : "Administrator";
            ps_student_list_export.push({
                "Username": student.username,
                "Name": student.name,
                "Permission": permission_str,
                "Answering Start Time": student.first_start_time_str,
                "Score": student.ps_actual_score,
            });
        });

        // console.log(ps_student_list_export);
        const worksheet = XLSX.utils.json_to_sheet(ps_student_list_export);
        const workbook = XLSX.utils.book_new();
        XLSX.utils.book_append_sheet(workbook, worksheet, "Transcripts");
        // Buffer to store the generated Excel file
        const excelBuffer = XLSX.write(workbook, { bookType: 'xlsx', type: 'array' });
        const blob = new Blob([excelBuffer], { type: 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet;charset=UTF-8' });

        const workbook_name = this.state.ps_name + "_Transcripts.xlsx";
        saveAs(blob, workbook_name);
    }

    handleAllStudentRecordRender = () => {
        // calculate the following 
        let student_absence = 0;  // 未作答人数，前端计算
        let student_presence = 0;  // 作答人数，前端计算
        let student_presence_average = 0;  // 作答学生平均分，前端计算
        const ps_student_list_sorted = [];

        this.state.ps_student_list.forEach((student) => {
            // 计算人数
            if (student.first_start_time === '') {
                student_absence += 1;
            } else {
                student_presence += 1;
            }

            // 计算总分
            const ps_actual_score = parseInt(student.ps_actual_score);
            student_presence_average += ps_actual_score;

            // 计算开始时间
            let first_start_time_str;
            if (student.first_start_time === '') {
                first_start_time_str = 'N/A';
            } else {
                const first_start_time = new Date(student.first_start_time);
                first_start_time_str = first_start_time.toLocaleString('zh-CN');
            }

            // 生成新列表
            ps_student_list_sorted.push({
                ...student,
                first_start_time_str: first_start_time_str,
                ps_actual_score: ps_actual_score,
                permission: parseInt(student.permission)
            });
        });

        // 计算平均分
        // console.log(student_presence_average);
        if (student_presence === 0) {
            student_presence_average = '--'
        } else {
            student_presence_average /= student_presence;
            student_presence_average = Math.round(student_presence_average * 100) / 100;
        }

        // 新列表按照实际得分降序
        ps_student_list_sorted.sort((a, b) => (b.ps_actual_score - a.ps_actual_score));

        // console.log(ps_student_list_sorted);

        // 根据学生成绩重新排名
        return (
            <div className="container-fluid">
                <ContendCard>
                    <div className="row">
                        <div className="col">
                            <h5>Student Transcripts</h5>
                        </div>
                        <div className="col-auto">
                            <button className='btn btn-sm btn-outline-primary' onClick={() => this.handleAllStudentRecordExport()}>Export to Excel</button>
                        </div>
                    </div>
                    <div className="row mt-2">
                        <div className="col text-center" style={{ fontWeight: 'bold' }}>
                            Absentees: {student_absence}
                        </div>
                        <div className="col text-center" style={{ fontWeight: 'bold' }}>
                            Participants: {student_presence}
                        </div>
                        <div className="col text-center" style={{ fontWeight: 'bold' }}>
                            Participants Average Score: {student_presence_average} / {this.state.ps_total_score}
                        </div>
                    </div>

                    {/* student table */}

                    <table className="table table-hover">
                        <thead>
                            <tr>
                                <th scope="col">Rank</th>
                                <th scope="col">Username</th>
                                <th scope="col">Name</th>
                                <th scope="col">Permission</th>
                                <th scope="col">Answering Start Time</th>
                                <th scope="col">Score</th>
                                <th scope="col">Operation</th>
                            </tr>
                        </thead>
                        <tbody>
                            {ps_student_list_sorted.map((student) => {
                                const index = ps_student_list_sorted.indexOf(student);
                                const permission = student.permission < 1 ? "Student" : student.permission < 2 ? "Teacher" : "Administrator";
                                const progress_bar_percentage = Math.round((student.ps_actual_score / this.state.ps_total_score) * 10000) / 100;
                                return (
                                    <tr key={'student' + student.user_id}>
                                        <th scope="row" style={{ width: "48px" }}>{index + 1}</th>
                                        <td style={{ width: "180px" }}>{student.username}</td>
                                        <td style={{ width: "180px" }}>{student.name}</td>
                                        <td style={{ width: "64px" }}>
                                            <span>{permission}</span>
                                        </td>
                                        <td style={{ width: "200px" }}>
                                            <span>{student.first_start_time_str}</span>
                                        </td>
                                        <td>
                                            <div className="row">
                                                <div className="col-xxl-8">
                                                    <div className="progress" role="progressbar" aria-label="Basic example" aria-valuenow="75" aria-valuemin="0" aria-valuemax="100" style={{ height: "10px", margin: "6px 0" }}>
                                                        <div className="progress-bar" style={{ width: `${progress_bar_percentage}%` }}></div>
                                                    </div>
                                                </div>
                                                <div className="col-xxl-4">
                                                    {student.ps_actual_score} / {this.state.ps_total_score}
                                                </div>
                                            </div>
                                        </td>
                                        <td style={{ width: "84px" }}>
                                            <Link className='link-primary' to={`/problem_set/teacher_view/one_record/${this.props.params.problem_set_id}/${student.user_id}/`} target='_blank'>Transcript</Link>
                                        </td>
                                    </tr>
                                );
                            })}
                        </tbody>
                    </table>
                </ContendCard>
            </div>
        );
    }

    handleObjectiveProblemRender = () => {
        return (
            <div className="container-fluid">
                <ContendCard>
                    <h5>Objective Problem Statistics</h5>

                    {/* student table */}
                    <table className="table table-hover">
                        <thead>
                            <tr>
                                <th scope="col">#</th>
                                <th scope="col">Description</th>
                                <th scope="col">Participants</th>
                                <th scope="col">Correct Count</th>
                                <th scope="col">Correct Rate</th>
                            </tr>
                        </thead>
                        <tbody>
                            {this.state.ps_objective_problem_list.map((objective_problem) => {
                                const index = this.state.ps_objective_problem_list.indexOf(objective_problem);
                                const op_answer_count = parseInt(objective_problem.op_answer_count);
                                const op_correct_count = parseInt(objective_problem.op_correct_count);
                                let objective_problem_correct_rate;
                                if (op_answer_count === 0) {
                                    objective_problem_correct_rate = 0;
                                } else {
                                    objective_problem_correct_rate = Math.round((op_correct_count / op_answer_count) * 10000) / 100;
                                }
                                return (
                                    <tr key={'op' + objective_problem.objective_problem_id}>
                                        <th scope='col' style={{ width: "48px" }}>{index + 1}</th>
                                        <td style={{ overflow: "hidden", textOverflow: "ellipsis", maxWidth: "8vw", minWidth: "8vw", textWrap: "nowrap" }}>
                                            <Link className='link-primary' to={`/problem_manage/objective_problem_manage/${objective_problem.objective_problem_id}`} target='_blank'>
                                                {objective_problem.op_description}
                                            </Link>
                                        </td>
                                        <td style={{ width: "112px" }}>
                                            {objective_problem.op_answer_count}
                                        </td>
                                        <td style={{ width: "140px" }}>
                                            {objective_problem.op_correct_count}
                                        </td>
                                        <td>
                                            <div className="row">
                                                <div className="col-xxl-9">
                                                    <div className="progress" role="progressbar" aria-label="Basic example" aria-valuenow="75" aria-valuemin="0" aria-valuemax="100" style={{ height: "10px", margin: "6px 0" }}>
                                                        <div className="progress-bar" style={{ width: `${objective_problem_correct_rate}%` }}></div>
                                                    </div>
                                                </div>
                                                <div className="col-xxl-3">
                                                    {`${op_answer_count === 0 ? "--" : objective_problem_correct_rate}%`}
                                                </div>
                                            </div>
                                        </td>
                                    </tr>
                                );
                            })}
                        </tbody>
                    </table>
                </ContendCard>
            </div>
        );
    }

    handleProgrammingRender = () => {
        return (
            <div className="container-fluid">
                <ContendCard>
                    <h5>Programming Problem Statistics</h5>

                    {/* student table */}
                    <table className="table table-hover">
                        <thead>
                            <tr>
                                <th scope="col">#</th>
                                <th scope="col">Title</th>
                                <th scope="col">Participants</th>
                                <th scope="col">Correct Count</th>
                                <th scope="col">Correct Rate</th>
                            </tr>
                        </thead>
                        <tbody>
                            {this.state.ps_programming_list.map((programming) => {
                                const index = this.state.ps_programming_list.indexOf(programming);
                                const p_answer_count = parseInt(programming.p_answer_count);
                                const p_correct_count = parseInt(programming.p_correct_count);
                                let programming_correct_rate;
                                if (p_answer_count === 0) {
                                    programming_correct_rate = 0;
                                } else {
                                    programming_correct_rate = Math.round((p_correct_count / p_answer_count) * 10000) / 100;
                                }
                                return (
                                    <tr key={'p' + programming.programming_id}>
                                        <th scope='col' style={{ width: "48px" }}>{index + 1}</th>
                                        <td style={{ overflow: "hidden", textOverflow: "ellipsis", maxWidth: "8vw", minWidth: "8vw", textWrap: "nowrap" }}>
                                            <Link className='link-primary' to={`/problem_manage/programming_manage/${programming.programming_id}`} target='_blank'>
                                                {programming.p_title}
                                            </Link>
                                        </td>
                                        <td style={{ width: "112px" }}>
                                            {programming.p_answer_count}
                                        </td>
                                        <td style={{ width: "140px" }}>
                                            {programming.p_correct_count}
                                        </td>
                                        <td>
                                            <div className="row">
                                                <div className="col-xxl-9">
                                                    <div className="progress" role="progressbar" aria-label="Basic example" aria-valuenow="75" aria-valuemin="0" aria-valuemax="100" style={{ height: "10px", margin: "6px 0" }}>
                                                        <div className="progress-bar" style={{ width: `${programming_correct_rate}%` }}></div>
                                                    </div>
                                                </div>
                                                <div className="col-xxl-3">
                                                    {`${p_answer_count === 0 ? "--" : programming_correct_rate}%`}
                                                </div>
                                            </div>
                                        </td>
                                    </tr>
                                );
                            })}
                        </tbody>
                    </table>
                </ContendCard>
            </div>
        );
    }

    handlePermissionRender = () => {
        if (this.props.permission > 0) {
            return (
                // real display of this page
                <div className="container">
                    <ContendCard>
                        <div className="row">
                            <div className="col-auto">
                                <button className='btn btn-outline-primary' onClick={() => this.handleNavigateBack()}>
                                    Back
                                </button>
                                <button className='btn btn-outline-primary ms-2' onClick={() => this.handleGetOneProblemSetInfo()}>
                                    Refresh
                                </button>
                            </div>
                            <div className="col">
                                <h4 className='text-center'>
                                    {this.state.ps_name} - Transcript
                                </h4>
                            </div>
                            <div className="col-auto text-end d-flex align-items-center justify-content-end" style={{ minWidth: "82px" }}>
                                <span style={{ color: "red" }}>{this.state.error_message}</span>
                                {this.handleLoadingRender()}
                            </div>
                        </div>

                        <hr className='do-not-set-margin' />

                        {this.handleProblemSetInfoRender()}

                        {this.handleAllStudentRecordRender()}

                        {this.handleObjectiveProblemRender()}

                        {this.handleProgrammingRender()}
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
                                        Problem Set ID={this.props.params.problem_set_id} Transcript
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
                                        Problem Set ID={this.props.params.problem_set_id} Transcript
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
        <ProblemSetAllRecordTeacher
            {...props}
            params={useParams()}
            navigate={useNavigate()}
        />
    )
);