import React, { Component } from 'react';
import { connect } from 'react-redux';
import { useNavigate, useParams } from 'react-router-dom';
import ContendCard from '../contents/ContentCard';
import { Link } from 'react-router-dom';
import $ from 'jquery';
import GET_INFO_TIMEOUT from '../config/GetInfoTimeoutConfig';
import BACKEND_ADDRESS_URL from "../config/BackendAddressURLConfig";

class SetPreview extends Component {
    state = {
        ps_name: '',  // 100
        ps_author_id: '',
        ps_start_time: '',
        ps_end_time: '',
        duration: '',
        ps_author_name: '',

        op_description_search: '',
        op_tag_search: '',
        op_difficulty_min_search: 1,
        op_difficulty_max_search: 5,
        op_searched: false,
        op_generated: false,
        ps_objective_problem_search_list: [
            // { objective_problem_id: '1', op_description: 'Question 1111111111111111111111111111111111111111111111111111111', op_tag: '无分类', op_use_count: '1' },
            // { objective_problem_id: '2', op_description: 'Question 2', op_tag: '无分类', op_use_count: '2' },
            // { objective_problem_id: '3', op_description: 'Question 3', op_tag: '无分类', op_use_count: '3' },
        ],
        ps_objective_problem_list: [
            // { objective_problem_id: '4', op_description: 'Question 4', op_tag: '无分类', op_use_count: '4' },
            // { objective_problem_id: '5', op_description: 'Question 5', op_tag: '无分类', op_use_count: '5' },
        ],
        op_random_count: 1,
        ps_objective_problem_random_select_list: [],

        p_title_search: '',
        p_tag_search: '',
        p_difficulty_min_search: 1,
        p_difficulty_max_search: 5,
        p_searched: false,
        p_generated: false,
        ps_programming_search_list: [
            // { programming_id: '1', p_title: 'Question 1111111111111111111111111111111111111111111111111111111', p_tag: '无分类', p_use_count: '1' },
            // { programming_id: '2', p_title: 'Question 2', p_tag: '无分类', p_use_count: '2' },
            // { programming_id: '3', p_title: 'Question 3', p_tag: '无分类', p_use_count: '3' },
        ],
        ps_programming_list: [
            // { programming_id: '4', p_title: 'Question 4', p_tag: '无分类', p_use_count: '4' },
            // { programming_id: '5', p_title: 'Question 5', p_tag: '无分类', p_use_count: '5' },
        ],
        p_random_count: 1,
        ps_programming_random_select_list: [],

        student_username_search: '',
        student_name_search: '',
        ps_student_search_list: [
            // { user_id: '1', name: 'yw', username: '01', permission: '2' },
            // { user_id: '2', name: 'yxc', username: '02', permission: '1' },
            // { user_id: '3', name: 'aa', username: '03', permission: '0' },
        ],
        ps_student_list: [
            // { user_id: '4', name: 'bb', username: '04', permission: '0' },
            // { user_id: '5', name: 'cc', username: '05', permission: '0' },
            // { user_id: '6', name: 'dd', username: '06', permission: '0' },
        ],
        student_searched: false,

        error_message: '',

        is_loading: false,
        disable_edit: true,  // 教师只能编辑自己创建的，管理员可以编辑所有人的

        ps_change: false,  // 是否对题目集信息作出修改，如果没有则不可以提交修改

        is_exam: true,  // 是否是考试
    }

    componentDidMount = () => {
        if (this.props.is_login) {
            this.handleGetProblemSetInfo();
            this.handleGetAddedObjectiveProblemList();
            this.handleGetAddedProgrammingList();
            this.handleGetAddedStudentList();
        } else {
            setTimeout(this.handleGetProblemSetInfo, GET_INFO_TIMEOUT);
            setTimeout(this.handleGetAddedObjectiveProblemList, GET_INFO_TIMEOUT);
            setTimeout(this.handleGetAddedProgrammingList, GET_INFO_TIMEOUT);
            setTimeout(this.handleGetAddedStudentList, GET_INFO_TIMEOUT);
        }
    }

    handleSubmit = () => {
        // console.log("submit");
        this.setState({
            error_message: '',
            is_loading: true
        });
        if (this.state.ps_name === '') {
            this.setState({
                error_message: 'The name cannot be empty',
                is_loading: false
            });
        } else if (this.state.ps_name.length > 100) {
            this.setState({
                error_message: 'The name cannot exceed 100 characters, current length: ' + this.state.ps_name.length,
                is_loading: false
            });
        } else if (this.state.ps_start_time === '') {
            this.setState({
                error_message: 'The start time cannot be empty',
                is_loading: false
            });
        } else if (this.state.ps_end_time === '') {
            this.setState({
                error_message: 'The end time cannot be empty',
                is_loading: false
            });
        } else if (Date.parse(this.state.ps_start_time) > Date.parse(this.state.ps_end_time)) {
            this.setState({
                error_message: 'The start time cannot be later than the end time',
                is_loading: false
            });
        } else if (this.state.duration === '') {
            this.setState({
                error_message: 'The duration cannot be empty',
                is_loading: false
            });
        } else if (this.state.is_exam && parseInt(this.state.duration) <= 0) {
            this.setState({
                error_message: 'The duration must be a positive integer',
                is_loading: false
            });
        } else if (this.state.is_exam && Date.parse(this.state.ps_start_time) + 1000 * 60 * parseInt(this.state.duration) > Date.parse(this.state.ps_end_time)) {
            this.setState({
                error_message: 'The duration is too long',
                is_loading: false
            });
        } else {
            // console.log("submit");
            const token = this.props.token;
            // console.log(token);
            $.ajax({
                url: BACKEND_ADDRESS_URL + "/set_manage/",
                type: "PUT",
                data: {
                    problemSetId: this.props.params.problem_set_id,
                    psName: this.state.ps_name,
                    psStartTime: this.state.ps_start_time,
                    psEndTime: this.state.ps_end_time,
                    duration: this.state.duration,
                },
                headers: {
                    Authorization: "Bearer " + token
                },
                success: (resp) => {
                    if (resp.error_message === "success") {
                        this.handleGetProblemSetInfo();
                        this.setState({
                            error_message: "Updated successfully",
                            is_loading: false,
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

    handleDelete = () => {
        // console.log("delete");
        this.setState({
            is_loading: true,
            error_message: ''
        });
        const token = this.props.token;
        // console.log(token);
        $.ajax({
            url: BACKEND_ADDRESS_URL + "/set_manage/",
            type: "DELETE",
            data: {
                problemSetId: this.props.params.problem_set_id,
            },
            headers: {
                Authorization: "Bearer " + token
            },
            success: (resp) => {
                // console.log(resp);
                if (resp.error_message === "success") {
                    // navigate
                    this.setState({
                        error_message: "Deleted successfully",
                        is_loading: false,
                    });
                    this.handleNavigateBack();
                } else {
                    this.setState({
                        error_message: resp.error_message,
                        is_loading: false,
                    });
                }
            },
        });
    }

    handleGetAddedStudentList = () => {
        const token = this.props.token;
        // console.log(token);
        // 先获取题目集信息，再获取题目信息，学生信息
        this.setState({
            is_loading: true,
        });

        $.ajax({
            url: BACKEND_ADDRESS_URL + "/set_manage/student/get_added/",
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
                    ps_student_list: resp,
                    is_loading: false,
                });
                if (resp.error_message) {
                    console.log(resp.error_message);
                }
            }
        });
    }

    handleGetAddedProgrammingList = () => {
        const token = this.props.token;
        // console.log(token);
        // 先获取题目集信息，再获取题目信息，学生信息
        this.setState({
            is_loading: true,
        });

        $.ajax({
            url: BACKEND_ADDRESS_URL + "/set_manage/programming/get_added/",
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

    handleGetAddedObjectiveProblemList = () => {
        const token = this.props.token;
        // console.log(token);
        // 先获取题目集信息，再获取题目信息，学生信息
        this.setState({
            is_loading: true,
        });

        $.ajax({
            url: BACKEND_ADDRESS_URL + "/set_manage/objective_problem/get_added/",
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

    handleGetProblemSetInfo = () => {
        // console.log("get one");
        const token = this.props.token;
        // console.log(token);
        // 先获取题目集信息，再获取题目信息，学生信息
        this.setState({
            is_loading: true,
            error_message: ''
        });
        $.ajax({
            url: BACKEND_ADDRESS_URL + "/set_manage/",
            type: "GET",
            data: {
                problemSetId: this.props.params.problem_set_id
            },
            headers: {
                Authorization: "Bearer " + token
            },
            success: (resp) => {
                // console.log(resp);
                if (resp.error_message === 'success') {
                    this.setState({
                        ps_change: false,
                        ps_name: resp.ps_name,
                        ps_author_name: resp.ps_author_name,
                        ps_author_id: resp.ps_author_id,
                        ps_start_time: resp.ps_start_time,
                        ps_end_time: resp.ps_end_time,
                        duration: resp.duration,
                    });

                    // decide editable
                    if (parseInt(resp.ps_author_id) === parseInt(this.props.user_id) || this.props.permission > 1) {
                        // console.log("editable");
                        this.setState({
                            disable_edit: false,
                        });
                    }

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

                    this.setState({
                        is_loading: false
                    });
                } else {
                    this.setState({
                        is_loading: false,
                        error_message: resp.error_message,
                        p_change: false
                    });
                }
            }
        });
    }

    handleTitleRender = () => {
        if (this.state.is_exam) {
            return (
                <h4>View an Exam</h4>
            );
        } else {
            return (
                <h4>View an Assignment</h4>
            );
        }
    }

    handleNavigateBack = () => {
        if (this.state.is_exam) {
            this.props.navigate("/set_manage/exam/");
        } else {
            this.props.navigate("/set_manage/assignment/");
        }
    }

    handleLoadingRender = () => {
        if (this.state.is_loading) {
            return (
                <span className="spinner-border spinner-border-sm me-2" aria-hidden="true"></span>
            );
        }
    }

    handleDeleteRender = () => {
        // 此函数渲染删除按钮和确认modal，目的是增强可读性。
        return (
            <React.Fragment>
                {/* <!-- Button trigger modal --> */}
                <button type="button" className="btn btn-danger float-end" data-bs-toggle="modal" data-bs-target="#deleteConfirmModal" disabled={this.state.is_loading || this.state.disable_edit} onClick={() => { this.setState({ error_message: '' }); }}>
                    {this.handleLoadingRender()}
                    Delete
                </button>

                {/* <!-- Modal --> */}
                <div className="modal fade" id="deleteConfirmModal" tabIndex="-1" aria-labelledby="deleteConfirmModalLabel" aria-hidden="true">
                    <div className="modal-dialog modal-dialog-centered">
                        <div className="modal-content">
                            <div className="modal-header">
                                <h1 className="modal-title fs-5" id="deleteConfirmModalLabel">Confirm Delete</h1>
                                <button type="button" className="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                            </div>
                            <div className="modal-body">
                                The deletion operation is irreversible. Do you confirm to delete the problem set?
                            </div>
                            <div className="modal-footer">
                                <span className='text-md-end' style={{ color: "red" }}>
                                    {this.state.error_message}
                                </span>
                                <button type="button" className="btn btn-secondary" data-bs-dismiss="modal">Close</button>
                                <button type="button" className="btn btn-danger" data-bs-dismiss="modal" onClick={() => this.handleDelete()}>
                                    {this.handleLoadingRender()}
                                    Confirm Delete
                                </button>
                            </div>
                        </div>
                    </div>
                </div>
            </React.Fragment>
        );
    }

    handleObjectiveProblemSearch = () => {
        // console.log("objective problem search");
        // console.log(this.state.op_description_search);
        // console.log(this.state.op_tag_search);
        const token = this.props.token;
        // console.log(token);
        this.setState({
            error_message: '',
            is_loading: true
        });
        $.ajax({
            url: BACKEND_ADDRESS_URL + "/set_manage/objective_problem/search/",
            type: "GET",
            data: {
                problemSetId: this.props.params.problem_set_id,
                opDescription: this.state.op_description_search,
                opTag: this.state.op_tag_search,
                opDifficultyMin: this.state.op_difficulty_min_search,
                opDifficultyMax: this.state.op_difficulty_max_search,
            },
            headers: {
                Authorization: "Bearer " + token
            },
            success: (resp) => {
                // console.log(resp);
                this.setState({
                    ps_objective_problem_search_list: resp,
                    is_loading: false,
                    op_searched: true,
                    op_random_count: 1,
                    ps_objective_problem_random_select_list: [],
                    op_generated: false,
                });
                if (resp.error_message) {
                    console.log(resp);
                }
            }
        });
    }

    handleObjectiveProblemAdd = (objective_problem) => {
        // console.log("add objective problem");
        // console.log(objective_problem.objective_problem_id);
        // 先联网添加，再前端添加
        this.setState({
            error_message: '',
            is_loading: true,
        });
        const token = this.props.token;
        $.ajax({
            url: BACKEND_ADDRESS_URL + "/set_manage/objective_problem/",
            type: "POST",
            data: {
                problemSetId: this.props.params.problem_set_id,
                objectiveProblemId: objective_problem.objective_problem_id
            },
            headers: {
                Authorization: "Bearer " + token
            },
            async: false,
            success: (resp) => {
                if (resp.error_message === "success") {
                    // 操作成功前端移除
                    const index = this.state.ps_objective_problem_search_list.indexOf(objective_problem);
                    // console.log(index);
                    // remove op from search list
                    // Do NOT use slice!!!
                    const new_ps_objective_problem_search_list = this.state.ps_objective_problem_search_list.toSpliced(index, 1);
                    // add op to list
                    const new_ps_objective_problem_list = this.state.ps_objective_problem_list;
                    new_ps_objective_problem_list.push({
                        ...objective_problem,
                        op_use_count: parseInt(objective_problem.op_use_count) + 1
                    });
                    this.setState({
                        ps_objective_problem_search_list: new_ps_objective_problem_search_list,
                        ps_objective_problem_list: new_ps_objective_problem_list,
                        error_message: "Objective problem added",
                        is_loading: false,
                        op_searched: false,
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

    handleObjectiveProblemRemove = (objective_problem) => {
        // console.log("remove objective problem");
        // console.log(objective_problem.objective_problem_id);
        // 先联网删除，再前端删除
        this.setState({
            error_message: '',
            is_loading: true,
        });
        const token = this.props.token;
        $.ajax({
            url: BACKEND_ADDRESS_URL + "/set_manage/objective_problem/",
            type: "DELETE",
            data: {
                problemSetId: this.props.params.problem_set_id,
                objectiveProblemId: objective_problem.objective_problem_id
            },
            headers: {
                Authorization: "Bearer " + token
            },
            success: (resp) => {
                if (resp.error_message === "success") {
                    // 操作成功前端
                    const index = this.state.ps_objective_problem_list.indexOf(objective_problem);
                    // remove from list
                    const new_ps_objective_problem_list = this.state.ps_objective_problem_list.toSpliced(index, 1);
                    // add to search list
                    const new_ps_objective_problem_search_list = this.state.ps_objective_problem_search_list;
                    new_ps_objective_problem_search_list.push({
                        ...objective_problem,
                        op_use_count: parseInt(objective_problem.op_use_count) - 1
                    });
                    this.setState({
                        ps_objective_problem_search_list: new_ps_objective_problem_search_list,
                        ps_objective_problem_list: new_ps_objective_problem_list,
                        error_message: "Objective problem dropped",
                        is_loading: false,
                        op_searched: false,
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

    handleObjectiveProblemRandomSelect = () => {
        // console.log("random add");
        this.setState({
            error_message: "",
        });
        // 检查输入
        const op_random_count = parseInt(this.state.op_random_count);
        if (op_random_count <= 0) {
            this.setState({
                error_message: "The number of problems must be a positive integer",
            });
        } else if (op_random_count > this.state.ps_objective_problem_search_list.length) {
            this.setState({
                error_message: "The number of problems cannot exceed the number of search results",
            });
        } else {
            const weight = 0.2;
            // 生成随机列表
            const ps_objective_problem_search_list_add_score = [];
            this.state.ps_objective_problem_search_list.forEach((objective_problem) => {
                const op_use_count = parseInt(objective_problem.op_use_count);
                const op_score = Math.random() - weight * op_use_count;
                ps_objective_problem_search_list_add_score.push({
                    ...objective_problem,
                    op_score: op_score
                });
            });
            ps_objective_problem_search_list_add_score.sort((a, b) => b.op_score - a.op_score);
            // console.log(ps_objective_problem_search_list_add_score);

            // 将排名前的n个取出存入结果数组
            this.setState({
                ps_objective_problem_random_select_list: ps_objective_problem_search_list_add_score.slice(0, op_random_count),
                op_generated: true,
            });
        }
    }

    handleObjectiveProblemRandomAdd = () => {
        // 无需清空错误信息和设置loading，调用的方法会处理
        this.state.ps_objective_problem_random_select_list.forEach((objective_problem) => {
            this.handleObjectiveProblemAdd(objective_problem);
        });
        this.setState({
            op_description_search: '',
            op_tag_search: '',
            op_searched: false,
            op_generated: false,
            ps_objective_problem_search_list: [],
        });

        // this.handleGetAddedObjectiveProblemList();
    }

    handleObjectiveProblemRandomModalRender = () => {
        return (
            <React.Fragment>
                <button type="button" className='btn btn-warning' disabled={this.state.is_loading || this.state.disable_edit || !this.state.op_searched} data-bs-toggle="modal" data-bs-target="#objectiveProblemRandomModal">
                    Random Add
                </button>
                {/* <!-- Button trigger modal --> */}

                {/* <!-- Modal --> */}
                <div className="modal fade" id="objectiveProblemRandomModal" tabIndex="-1" aria-labelledby="objectiveProblemRandomModalLabel" aria-hidden="true">
                    <div className="modal-dialog modal-dialog-centered modal-dialog-scrollable modal-lg">
                        <div className="modal-content">
                            <div className="modal-header">
                                <h1 className="modal-title fs-5" id="objectiveProblemRandomModalLabel">Random Add</h1>
                                <button type="button" className="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                            </div>
                            <div className="modal-body">
                                <div className="container-fluid">
                                    {/* tool bar */}
                                    <div className="row">
                                        <div className="col-md-8">
                                            <div className="row">
                                                <div className="col-auto">
                                                    <label htmlFor="op_random_count" className="col-form-label">Number of Problems</label>
                                                </div>
                                                <div className="col">
                                                    <input type="number" className="form-control" id="op_random_count" value={this.state.op_random_count} onChange={(e) => { this.setState({ op_random_count: e.target.value }); }} disabled={this.state.is_loading || this.state.disable_edit} min={1} max={this.state.ps_objective_problem_search_list.length} />
                                                </div>
                                            </div>
                                        </div>
                                        <div className="col-md-4">
                                            <div className="d-flex justify-content-around">
                                                <button className='btn btn-warning' onClick={() => this.handleObjectiveProblemRandomSelect()} disabled={this.state.is_loading || this.state.disable_edit || !this.state.op_searched} >
                                                    Generate
                                                </button>
                                                <button className='btn btn-outline-success' disabled={this.state.is_loading || this.state.disable_edit || !this.state.op_searched || !this.state.op_generated} onClick={() => this.handleObjectiveProblemRandomAdd()}>
                                                    Add All
                                                </button>
                                            </div>
                                        </div>
                                    </div>

                                    {/* random table */}
                                    <table className='table table-sm table-hover'>
                                        <thead>
                                            <tr>
                                                <th scope="col">Description</th>
                                                <th scope="col">Tag</th>
                                                <th scope="col">Difficulty</th>
                                                <th scope="col">Use Count</th>
                                            </tr>
                                        </thead>
                                        <tbody>
                                            {this.state.ps_objective_problem_random_select_list.map((objective_problem) => {
                                                return (
                                                    <tr key={'op_random' + objective_problem.objective_problem_id}>
                                                        <td style={{ overflow: "hidden", textOverflow: "ellipsis", maxWidth: "15vw", minWidth: "15vw", textWrap: "nowrap" }}>
                                                            <Link className='link-primary' to={`/problem_manage/objective_problem_manage/${objective_problem.objective_problem_id}`} target='_blank'>
                                                                {objective_problem.op_description}
                                                            </Link>
                                                        </td>
                                                        <td>{objective_problem.op_tag}</td>
                                                        <td>{objective_problem.op_difficulty}</td>
                                                        <td>{objective_problem.op_use_count}</td>
                                                    </tr>
                                                );
                                            })}
                                        </tbody>
                                    </table>
                                </div>
                            </div>
                            <div className="modal-footer">
                                <span className='text-md-end' style={{ color: "red" }}>
                                    {this.state.error_message}
                                </span>
                                <button type="button" className="btn btn-secondary" data-bs-dismiss="modal">Close</button>
                            </div>
                        </div>
                    </div>
                </div>
            </React.Fragment>
        );
    }

    handleProgrammingSearch = () => {
        // console.log("programming search");
        // console.log(this.state.p_title_search);
        // console.log(this.state.p_tag_search);
        const token = this.props.token;
        // console.log(token);
        this.setState({
            error_message: '',
            is_loading: true
        });
        $.ajax({
            url: BACKEND_ADDRESS_URL + "/set_manage/programming/search/",
            type: "GET",
            data: {
                problemSetId: this.props.params.problem_set_id,
                pTitle: this.state.p_title_search,
                pTag: this.state.p_tag_search,
                pDifficultyMin: this.state.p_difficulty_min_search,
                pDifficultyMax: this.state.p_difficulty_max_search,
            },
            headers: {
                Authorization: "Bearer " + token
            },
            success: (resp) => {
                // console.log(resp);
                this.setState({
                    ps_programming_search_list: resp,
                    is_loading: false,
                    p_searched: true,
                    p_random_count: 1,
                    ps_programming_random_select_list: [],
                    p_generated: false,
                });
                if (resp.error_message) {
                    console.log(resp);
                }
            }
        });
    }

    handleProgrammingAdd = (programming) => {
        // 先联网添加，再前端添加
        this.setState({
            error_message: '',
            is_loading: true,
        });
        const token = this.props.token;
        $.ajax({
            url: BACKEND_ADDRESS_URL + "/set_manage/programming/",
            type: "POST",
            data: {
                problemSetId: this.props.params.problem_set_id,
                programmingId: programming.programming_id
            },
            headers: {
                Authorization: "Bearer " + token
            },
            async: false,
            success: (resp) => {
                if (resp.error_message === "success") {
                    // 操作成功前端移除
                    const index = this.state.ps_programming_search_list.indexOf(programming);
                    // console.log(index);
                    // remove p from search list
                    // Do NOT use slice!!!
                    const new_ps_programming_search_list = this.state.ps_programming_search_list.toSpliced(index, 1);
                    // add p to list
                    const new_ps_programming_list = this.state.ps_programming_list;
                    new_ps_programming_list.push({
                        ...programming,
                        p_use_count: parseInt(programming.p_use_count) + 1
                    });
                    this.setState({
                        ps_programming_search_list: new_ps_programming_search_list,
                        ps_programming_list: new_ps_programming_list,
                        error_message: "Programming problem added",
                        is_loading: false,
                        p_searched: false,
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

    handleProgrammingRemove = (programming) => {
        // 先联网删除，再前端删除
        this.setState({
            error_message: '',
            is_loading: true,
        });
        const token = this.props.token;
        $.ajax({
            url: BACKEND_ADDRESS_URL + "/set_manage/programming/",
            type: "DELETE",
            data: {
                problemSetId: this.props.params.problem_set_id,
                programmingId: programming.programming_id
            },
            headers: {
                Authorization: "Bearer " + token
            },
            success: (resp) => {
                if (resp.error_message === "success") {
                    // 操作成功前端
                    const index = this.state.ps_programming_list.indexOf(programming);
                    // remove from list
                    const new_ps_programming_list = this.state.ps_programming_list.toSpliced(index, 1);
                    // add to search list
                    const new_ps_programming_search_list = this.state.ps_programming_search_list;
                    new_ps_programming_search_list.push({
                        ...programming,
                        p_use_count: parseInt(programming.p_use_count) - 1
                    });
                    this.setState({
                        ps_programming_search_list: new_ps_programming_search_list,
                        ps_programming_list: new_ps_programming_list,
                        error_message: "Programming problem dropped",
                        is_loading: false,
                        p_searched: false,
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

    handleProgrammingRandomSelect = () => {
        this.setState({
            error_message: "",
        });
        // 检查输入
        const p_random_count = parseInt(this.state.p_random_count);
        if (p_random_count <= 0) {
            this.setState({
                error_message: "The number of problems must be a positive integer",
            });
        } else if (p_random_count > this.state.ps_programming_search_list.length) {
            this.setState({
                error_message: "The number of problems cannot exceed the number of search results",
            });
        } else {
            const weight = 0.2;
            // 生成随机列表
            const ps_programming_search_list_add_score = [];
            this.state.ps_programming_search_list.forEach((programming) => {
                const p_use_count = parseInt(programming.p_use_count);
                const p_score = Math.random() - weight * p_use_count;
                ps_programming_search_list_add_score.push({
                    ...programming,
                    p_score: p_score
                });
            });
            ps_programming_search_list_add_score.sort((a, b) => b.p_score - a.p_score);
            // console.log(ps_programming_search_list_add_score);

            // 将排名前的n个取出存入结果数组
            this.setState({
                ps_programming_random_select_list: ps_programming_search_list_add_score.slice(0, p_random_count),
                p_generated: true,
            });
        }
    }

    handleProgrammingRandomAdd = () => {
        // 无需清空错误信息和设置loading，调用的方法会处理
        this.state.ps_programming_random_select_list.forEach((programming) => {
            this.handleProgrammingAdd(programming);
        });
        this.setState({
            p_title_search: '',
            p_tag_search: '',
            p_searched: false,
            p_generated: false,
            ps_programming_search_list: [],
        });

        // this.handleGetAddedProgrammingList();
    }

    handleProgrammingRandomModalRender = () => {
        return (
            <React.Fragment>
                <button type="button" className='btn btn-warning' disabled={this.state.is_loading || this.state.disable_edit || !this.state.p_searched} data-bs-toggle="modal" data-bs-target="#programmingRandomModal">
                    Random Add
                </button>
                {/* <!-- Button trigger modal --> */}

                {/* <!-- Modal --> */}
                <div className="modal fade" id="programmingRandomModal" tabIndex="-1" aria-labelledby="programmingRandomModalLabel" aria-hidden="true">
                    <div className="modal-dialog modal-dialog-centered modal-dialog-scrollable modal-lg">
                        <div className="modal-content">
                            <div className="modal-header">
                                <h1 className="modal-title fs-5" id="programmingRandomModalLabel">Random Add</h1>
                                <button type="button" className="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                            </div>
                            <div className="modal-body">
                                <div className="container-fluid">
                                    {/* tool bar */}
                                    <div className="row">
                                        <div className="col-md-8">
                                            <div className="row">
                                                <div className="col-auto">
                                                    <label htmlFor="p_random_count" className="col-form-label">Number of Problems</label>
                                                </div>
                                                <div className="col">
                                                    <input type="number" className="form-control" id="p_random_count" value={this.state.p_random_count} onChange={(e) => { this.setState({ p_random_count: e.target.value }); }} disabled={this.state.is_loading || this.state.disable_edit} min={1} max={this.state.ps_programming_search_list.length} />
                                                </div>
                                            </div>
                                        </div>
                                        <div className="col-md-4">
                                            <div className="d-flex justify-content-around">
                                                <button className='btn btn-warning' onClick={() => this.handleProgrammingRandomSelect()} disabled={this.state.is_loading || this.state.disable_edit || !this.state.p_searched} >
                                                    Generate
                                                </button>
                                                <button className='btn btn-outline-success' disabled={this.state.is_loading || this.state.disable_edit || !this.state.p_searched || !this.state.p_generated} onClick={() => this.handleProgrammingRandomAdd()}>
                                                    Add All
                                                </button>
                                            </div>
                                        </div>
                                    </div>

                                    {/* random table */}
                                    <table className='table table-sm table-hover'>
                                        <thead>
                                            <tr>
                                                <th scope="col">Title</th>
                                                <th scope="col">Tag</th>
                                                <th scope="col">Difficulty</th>
                                                <th scope="col">Use Count</th>
                                            </tr>
                                        </thead>
                                        <tbody>
                                            {this.state.ps_programming_random_select_list.map((programming) => {
                                                return (
                                                    <tr key={'p_random' + programming.programming_id}>
                                                        <td style={{ overflow: "hidden", textOverflow: "ellipsis", maxWidth: "15vw", minWidth: "15vw", textWrap: "nowrap" }}>
                                                            <Link className='link-primary' to={`/problem_manage/programming_manage/${programming.programming_id}`} target='_blank'>
                                                                {programming.p_title}
                                                            </Link>
                                                        </td>
                                                        <td>{programming.p_tag}</td>
                                                        <td>{programming.p_difficulty}</td>
                                                        <td>{programming.p_use_count}</td>
                                                    </tr>
                                                );
                                            })}
                                        </tbody>
                                    </table>
                                </div>
                            </div>
                            <div className="modal-footer">
                                <span className='text-md-end' style={{ color: "red" }}>
                                    {this.state.error_message}
                                </span>
                                <button type="button" className="btn btn-secondary" data-bs-dismiss="modal">Close</button>
                            </div>
                        </div>
                    </div>
                </div>
            </React.Fragment>
        );
    }

    handleProblemRender = () => {
        return (
            <React.Fragment>
                <h4>Add Problems</h4>
                {/* problem Accordion */}
                <div className="accordion" id="problemManageForProblemSet">
                    <div className="accordion-item">
                        <h2 className="accordion-header">
                            <button className="accordion-button" type="button" data-bs-toggle="collapse" data-bs-target="#objectiveProblem" aria-expanded="true" aria-controls="objectiveProblem">
                                Objective Problems: {this.state.ps_objective_problem_list.length}
                            </button>
                        </h2>
                        <div id="objectiveProblem" className="accordion-collapse collapse show" >
                            <div className="accordion-body">
                                <div className="container-fluid">
                                    <div className="row">
                                        <div className="col-md-3">
                                            <div className="row">
                                                <div className="col-auto">
                                                    <label htmlFor="op_description_search" className="col-form-label">Description</label>
                                                </div>
                                                <div className="col">
                                                    <input type="search" className="form-control" id="op_description_search" value={this.state.op_description_search} onChange={(e) => { this.setState({ op_description_search: e.target.value }); }} disabled={this.state.is_loading || this.state.disable_edit} placeholder='Support fuzzy search' />
                                                </div>
                                            </div>
                                        </div>
                                        <div className="col-md-3">
                                            <div className="row">
                                                <div className="col-auto">
                                                    <label htmlFor="op_tag_search" className="col-form-label">Tag</label>
                                                </div>
                                                <div className="col">
                                                    <input type="search" className="form-control" id="op_tag_search" value={this.state.op_tag_search} onChange={(e) => { this.setState({ op_tag_search: e.target.value }); }} disabled={this.state.is_loading || this.state.disable_edit} placeholder='Support fuzzy search' />
                                                </div>
                                            </div>
                                        </div>
                                        <div className="col-md-3">
                                            <div className="row">
                                                <div className="col-auto">
                                                    <label htmlFor="op_difficulty_search" className="col-form-label">Difficulty Range</label>
                                                </div>
                                                <div className="col" id="op_difficulty_search">
                                                    <div className="input-group">
                                                        <input type="number" className="form-control" value={this.state.op_difficulty_min_search} onChange={(e) => { this.setState({ op_difficulty_min_search: e.target.value }); }} min={1} max={5} disabled={this.state.is_loading || this.state.disable_edit} />
                                                        <span className="input-group-text">~</span>
                                                        <input type="number" className="form-control" value={this.state.op_difficulty_max_search} onChange={(e) => { this.setState({ op_difficulty_max_search: e.target.value }); }} min={1} max={5} disabled={this.state.is_loading || this.state.disable_edit} />
                                                    </div>
                                                </div>
                                            </div>
                                        </div>
                                        <div className="col-md-3">
                                            <div className="d-flex justify-content-around">
                                                <button className='btn btn-success me-2' disabled={this.state.is_loading || this.state.disable_edit} onClick={() => this.handleObjectiveProblemSearch()}>
                                                    {this.handleLoadingRender()}
                                                    Search
                                                </button>
                                                {/* <button className='btn btn-secondary me-4' disabled={this.state.is_loading || this.state.disable_edit} onClick={() => { this.setState({ op_description_search: '', op_tag_search: '', error_message: '' }) }}>
                                                    {this.handleLoadingRender()}
                                                    清除
                                                </button> */}
                                                {this.handleObjectiveProblemRandomModalRender()}
                                            </div>
                                        </div>
                                    </div>
                                </div>
                                <hr />
                                <div className="container-fluid">
                                    <div className="row">
                                        <div className="col-md-6">
                                            <h5>Search Result</h5>
                                            {/* ps_objective_problem_search_list table */}
                                            <table className='table table-sm table-hover'>
                                                <thead>
                                                    <tr>
                                                        <th scope="col">Description</th>
                                                        <th scope="col">Tag</th>
                                                        <th scope="col">Difficulty</th>
                                                        <th scope="col">Use Count</th>
                                                        <th scope="col">Operation</th>
                                                    </tr>
                                                </thead>
                                                <tbody>
                                                    {this.state.ps_objective_problem_search_list.map((objective_problem) => {
                                                        return (
                                                            <tr key={'op' + objective_problem.objective_problem_id}>
                                                                <td className='align-middle' style={{ overflow: "hidden", textOverflow: "ellipsis", maxWidth: "15vw", minWidth: "15vw", textWrap: "nowrap" }}>
                                                                    <Link className='link-primary' to={`/problem_manage/objective_problem_manage/${objective_problem.objective_problem_id}`} target='_blank'>
                                                                        {objective_problem.op_description}
                                                                    </Link>
                                                                </td>
                                                                <td className='align-middle'>{objective_problem.op_tag}</td>
                                                                <td className='align-middle'>{objective_problem.op_difficulty}</td>
                                                                <td className='align-middle'>{objective_problem.op_use_count}</td>
                                                                <td className='align-middle'>
                                                                    <button className='btn btn-sm btn-outline-success' disabled={this.state.is_loading || this.state.disable_edit} onClick={() => this.handleObjectiveProblemAdd(objective_problem)}>
                                                                        {this.handleLoadingRender()}
                                                                        Add
                                                                    </button>
                                                                </td>
                                                            </tr>
                                                        );
                                                    })}
                                                </tbody>
                                            </table>
                                        </div>
                                        <div className="col-md-6">
                                            <h5>Added Problems</h5>
                                            {/* ps_objective_problem_list table */}
                                            <table className='table table-sm table-hover'>
                                                <thead>
                                                    <tr>
                                                        <th scope="col">Description</th>
                                                        <th scope="col">Tag</th>
                                                        <th scope="col">Difficulty</th>
                                                        <th scope="col">Use Count</th>
                                                        <th scope="col">Operation</th>
                                                    </tr>
                                                </thead>
                                                <tbody>
                                                    {this.state.ps_objective_problem_list.map((objective_problem) => {
                                                        return (
                                                            <tr key={'op' + objective_problem.objective_problem_id}>
                                                                <td className='align-middle' style={{ overflow: "hidden", textOverflow: "ellipsis", maxWidth: "15vw", minWidth: "15vw", textWrap: "nowrap" }}>
                                                                    <Link className='link-primary' to={`/problem_manage/objective_problem_manage/${objective_problem.objective_problem_id}`} target='_blank'>
                                                                        {objective_problem.op_description}
                                                                    </Link>
                                                                </td>
                                                                <td className='align-middle'>{objective_problem.op_tag}</td>
                                                                <td className='align-middle'>{objective_problem.op_difficulty}</td>
                                                                <td className='align-middle'>{objective_problem.op_use_count}</td>
                                                                <td className='align-middle'>
                                                                    <button className='btn btn-sm btn-outline-danger' disabled={this.state.is_loading || this.state.disable_edit} onClick={() => this.handleObjectiveProblemRemove(objective_problem)}>
                                                                        {this.handleLoadingRender()}
                                                                        Drop
                                                                    </button>
                                                                </td>
                                                            </tr>
                                                        );
                                                    })}
                                                </tbody>
                                            </table>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                    <div className="accordion-item">
                        <h2 className="accordion-header">
                            <button className="accordion-button" type="button" data-bs-toggle="collapse" data-bs-target="#programming" aria-expanded="true" aria-controls="programming">
                                Programming Problems: {this.state.ps_programming_list.length}
                            </button>
                        </h2>
                        <div id="programming" className="accordion-collapse collapse show">
                            <div className="accordion-body">
                                <div className="container-fluid">
                                    <div className="row">
                                        <div className="col-md-3">
                                            <div className="row">
                                                <div className="col-auto">
                                                    <label htmlFor="p_title_search" className="col-form-label">Title</label>
                                                </div>
                                                <div className="col">
                                                    <input type="search" className="form-control" id="p_title_search" value={this.state.p_title_search} onChange={(e) => { this.setState({ p_title_search: e.target.value }); }} disabled={this.state.is_loading || this.state.disable_edit} placeholder='Support fuzzy search' />
                                                </div>
                                            </div>
                                        </div>
                                        <div className="col-md-3">
                                            <div className="row">
                                                <div className="col-auto">
                                                    <label htmlFor="p_tag_search" className="col-form-label">Tag</label>
                                                </div>
                                                <div className="col">
                                                    <input type="search" className="form-control" id="p_tag_search" value={this.state.p_tag_search} onChange={(e) => { this.setState({ p_tag_search: e.target.value }); }} disabled={this.state.is_loading || this.state.disable_edit} placeholder='Support fuzzy search' />
                                                </div>
                                            </div>
                                        </div>
                                        <div className="col-md-3">
                                            <div className="row">
                                                <div className="col-auto">
                                                    <label htmlFor="op_difficulty_search" className="col-form-label">Difficulty Range</label>
                                                </div>
                                                <div className="col" id="op_difficulty_search">
                                                    <div className="input-group">
                                                        <input type="number" className="form-control" value={this.state.p_difficulty_min_search} onChange={(e) => { this.setState({ p_difficulty_min_search: e.target.value }); }} min={1} max={5} disabled={this.state.is_loading || this.state.disable_edit} />
                                                        <span className="input-group-text">~</span>
                                                        <input type="number" className="form-control" value={this.state.p_difficulty_max_search} onChange={(e) => { this.setState({ p_difficulty_max_search: e.target.value }); }} min={1} max={5} disabled={this.state.is_loading || this.state.disable_edit} />
                                                    </div>
                                                </div>
                                            </div>
                                        </div>
                                        <div className="col-md-3">
                                            <div className="d-flex justify-content-around">
                                                <button className='btn btn-success me-2' disabled={this.state.is_loading || this.state.disable_edit} onClick={() => this.handleProgrammingSearch()}>
                                                    {this.handleLoadingRender()}
                                                    Search
                                                </button>
                                                {/* <button className='btn btn-secondary me-4' disabled={this.state.is_loading || this.state.disable_edit} onClick={() => { this.setState({ p_title_search: '', p_tag_search: '', error_message: '' }) }}>
                                                    {this.handleLoadingRender()}
                                                    清除
                                                </button> */}
                                                {this.handleProgrammingRandomModalRender()}
                                            </div>
                                        </div>
                                    </div>
                                </div>
                                <hr />
                                <div className="container-fluid">
                                    <div className="row">
                                        <div className="col-md-6">
                                            <h5>Search Result</h5>
                                            {/* ps_programming_search_list table */}
                                            <table className='table table-sm table-hover'>
                                                <thead>
                                                    <tr>
                                                        <th scope="col">Title</th>
                                                        <th scope="col">Tag</th>
                                                        <th scope="col">Difficulty</th>
                                                        <th scope="col">Use Count</th>
                                                        <th scope="col">Operation</th>
                                                    </tr>
                                                </thead>
                                                <tbody>
                                                    {this.state.ps_programming_search_list.map((programming) => {
                                                        return (
                                                            <tr key={'p' + programming.programming_id}>
                                                                <td className='align-middle' style={{ overflow: "hidden", textOverflow: "ellipsis", maxWidth: "15vw", minWidth: "15vw", textWrap: "nowrap" }}>
                                                                    <Link className='link-primary' to={`/problem_manage/programming_manage/${programming.programming_id}`} target='_blank'>
                                                                        {programming.p_title}
                                                                    </Link>
                                                                </td>
                                                                <td className='align-middle'>{programming.p_tag}</td>
                                                                <td className='align-middle'>{programming.p_difficulty}</td>
                                                                <td className='align-middle'>{programming.p_use_count}</td>
                                                                <td className='align-middle'>
                                                                    <button className='btn btn-sm btn-outline-success' disabled={this.state.is_loading || this.state.disable_edit} onClick={() => this.handleProgrammingAdd(programming)}>
                                                                        {this.handleLoadingRender()}
                                                                        Add
                                                                    </button>
                                                                </td>
                                                            </tr>
                                                        );
                                                    })}
                                                </tbody>
                                            </table>
                                        </div>
                                        <div className="col-md-6">
                                            <h5>Added Problems</h5>
                                            {/* ps_programming_list table */}
                                            <table className='table table-sm table-hover'>
                                                <thead>
                                                    <tr>
                                                        <th scope="col">Description</th>
                                                        <th scope="col">Tag</th>
                                                        <th scope="col">Difficulty</th>
                                                        <th scope="col">Use Count</th>
                                                        <th scope="col">Operation</th>
                                                    </tr>
                                                </thead>
                                                <tbody>
                                                    {this.state.ps_programming_list.map((programming) => {
                                                        return (
                                                            <tr key={'p' + programming.programming_id}>
                                                                <td className='align-middle' style={{ overflow: "hidden", textOverflow: "ellipsis", maxWidth: "15vw", minWidth: "15vw", textWrap: "nowrap" }}>
                                                                    <Link className='link-primary' to={`/problem_manage/programming_manage/${programming.programming_id}`} target='_blank'>
                                                                        {programming.p_title}
                                                                    </Link>
                                                                </td>
                                                                <td className='align-middle'>{programming.p_tag}</td>
                                                                <td className='align-middle'>{programming.p_difficulty}</td>
                                                                <td className='align-middle'>{programming.p_use_count}</td>
                                                                <td className='align-middle'>
                                                                    <button className='btn btn-sm btn-outline-danger' disabled={this.state.is_loading || this.state.disable_edit} onClick={() => this.handleProgrammingRemove(programming)}>
                                                                        {this.handleLoadingRender()}
                                                                        Drop
                                                                    </button>
                                                                </td>
                                                            </tr>
                                                        );
                                                    })}
                                                </tbody>
                                            </table>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>

                <hr />
            </React.Fragment>
        );
    }

    handleStudentSearch = () => {
        // console.log("search");
        // console.log(this.state.student_username_search);
        // console.log(this.state.student_name_search);
        const token = this.props.token;
        // console.log(token);
        this.setState({
            error_message: '',
            is_loading: true
        });
        $.ajax({
            url: BACKEND_ADDRESS_URL + "/set_manage/student/search/",
            type: "GET",
            data: {
                problemSetId: this.props.params.problem_set_id,
                username: this.state.student_username_search,
                name: this.state.student_name_search,
            },
            headers: {
                Authorization: "Bearer " + token
            },
            success: (resp) => {
                // console.log(resp);
                this.setState({
                    ps_student_search_list: resp,
                    is_loading: false,
                    student_searched: true,
                });
                if (resp.error_message) {
                    console.log(resp);
                }
            }
        });
    }

    handleStudentAdd = (student) => {
        // console.log("add");
        // 先联网添加，再前端添加
        this.setState({
            error_message: '',
            is_loading: true,
        });
        const token = this.props.token;
        $.ajax({
            url: BACKEND_ADDRESS_URL + "/set_manage/student/",
            type: "POST",
            data: {
                problemSetId: this.props.params.problem_set_id,
                userId: student.user_id
            },
            headers: {
                Authorization: "Bearer " + token
            },
            async: false,
            success: (resp) => {
                if (resp.error_message === "success") {
                    // 操作成功前端移除
                    const index = this.state.ps_student_search_list.indexOf(student);
                    // console.log(index);
                    // remove p from search list
                    // Do NOT use slice!!!
                    const new_ps_student_search_list = this.state.ps_student_search_list.toSpliced(index, 1);
                    // add p to list
                    const new_ps_student_list = this.state.ps_student_list;
                    new_ps_student_list.push({
                        ...student
                    });
                    this.setState({
                        ps_student_search_list: new_ps_student_search_list,
                        ps_student_list: new_ps_student_list,
                        error_message: "Student added",
                        is_loading: false,
                        student_searched: false,
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

    handleStudentRemove = (student) => {
        // console.log("remove");
        // 先联网删除，再前端删除
        this.setState({
            error_message: '',
            is_loading: true,
        });
        const token = this.props.token;
        $.ajax({
            url: BACKEND_ADDRESS_URL + "/set_manage/student/",
            type: "DELETE",
            data: {
                problemSetId: this.props.params.problem_set_id,
                userId: student.user_id
            },
            headers: {
                Authorization: "Bearer " + token
            },
            success: (resp) => {
                if (resp.error_message === "success") {
                    // 操作成功前端
                    const index = this.state.ps_student_list.indexOf(student);
                    // remove from list
                    const new_ps_student_list = this.state.ps_student_list.toSpliced(index, 1);
                    // add to search list
                    const new_ps_student_search_list = this.state.ps_student_search_list;
                    new_ps_student_search_list.push({
                        ...student,
                    });
                    this.setState({
                        ps_student_search_list: new_ps_student_search_list,
                        ps_student_list: new_ps_student_list,
                        error_message: "Student dropped",
                        is_loading: false,
                        student_searched: false,
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

    handleStudentAddAll = () => {
        // console.log("add all");
        // 无需清空错误信息和设置loading，调用的方法会处理
        this.state.ps_student_search_list.forEach((student) => {
            this.handleStudentAdd(student);
        })
        this.setState({
            student_username_search: '',
            student_name_search: '',
            student_searched: false,
            ps_student_search_list: [],
        });

        // this.handleGetAddedStudentList();
    }

    handleStudentRender = () => {
        return (
            <React.Fragment>
                <h4>Add Students</h4>
                <ContendCard>
                    <div className="container-fluid">
                        <div className="row">
                            <div className="col-md-2 d-flex align-items-center">
                                <div>Students: {this.state.ps_student_list.length}</div>
                            </div>
                            <div className="col-md-4">
                                <div className="row">
                                    <div className="col-auto">
                                        <label htmlFor="studentUsernameSearch" className="col-form-label">Username</label>
                                    </div>
                                    <div className="col">
                                        <input type="search" className="form-control" id="studentUsernameSearch"
                                            value={this.state.student_username_search}
                                            onChange={(e) => {
                                                this.setState({ student_username_search: e.target.value });
                                            }}
                                            disabled={this.state.is_loading || this.state.disable_edit}
                                            placeholder='Support fuzzy search' />
                                    </div>
                                </div>
                            </div>
                            <div className="col-md-4">
                                <div className="row">
                                    <div className="col-auto">
                                        <label htmlFor="studentNameSearch" className="col-form-label">Name
                                        </label>
                                    </div>
                                    <div className="col">
                                        <input type="search" className="form-control" id="studentNameSearch"
                                            value={this.state.student_name_search}
                                            onChange={(e) => {
                                                this.setState({ student_name_search: e.target.value });
                                            }}
                                            disabled={this.state.is_loading || this.state.disable_edit}
                                            placeholder='Support fuzzy search' />
                                    </div>
                                </div>
                            </div>
                            <div className="col-md-2 d-flex justify-content-around">
                                <button className='btn btn-success me-1' disabled={this.state.is_loading || this.state.disable_edit} onClick={() => this.handleStudentSearch()}>Search
                                </button>
                                {/* <button className='btn btn-secondary me-1' disabled={this.state.is_loading || this.state.disable_edit} onClick={() => {
                                    this.setState({
                                        student_name_search: '',
                                        student_username_search: '',
                                        error_message: '',
                                    });
                                }}>清除
                                </button> */}
                                <button className='btn btn-warning' disabled={this.state.is_loading || this.state.disable_edit || !this.state.student_searched} onClick={() => this.handleStudentAddAll()}>Add All</button>
                            </div>
                        </div>
                    </div>
                    <hr />
                    <div className="container-fluid">
                        <div className="row">
                            <div className="col-md-6">
                                <h5>Search Result</h5>

                                {/* ps_student_search_list table */}

                                <table className='table table-sm table-hover'>
                                    <thead>
                                        <tr>
                                            <th scope="col">Username</th>
                                            <th scope="col">Name</th>
                                            <th scope="col">Permission</th>
                                            <th scope="col">Operation</th>
                                        </tr>
                                    </thead>
                                    <tbody>
                                        {this.state.ps_student_search_list.map((student) => {
                                            const permission = parseInt(student.permission);
                                            const permission_str = permission < 1 ? "Student" : permission < 2 ? "Teacher" : "Administrator";
                                            const style = { overflowX: "auto", overflowY: "none", maxWidth: "15vw", textWrap: "nowrap", minWidth: "10vw" };
                                            return (
                                                <tr key={'student' + student.user_id}>
                                                    <td className='align-middle' style={style}>
                                                        {student.username}
                                                    </td>
                                                    <td className='align-middle' style={style}>
                                                        {student.name}
                                                    </td>
                                                    <td className='align-middle'>{permission_str}</td>
                                                    <td className='align-middle'>
                                                        <button className='btn btn-sm btn-outline-success' disabled={this.state.is_loading || this.state.disable_edit} onClick={() => this.handleStudentAdd(student)}>
                                                            {this.handleLoadingRender()}
                                                            Add
                                                        </button>
                                                    </td>
                                                </tr>
                                            );
                                        })}
                                    </tbody>
                                </table>
                            </div>
                            <div className="col-md-6">
                                <h5>Added Students</h5>

                                {/* ps_student_list table */}
                                <table className='table table-sm table-hover'>
                                    <thead>
                                        <tr>
                                            <th scope="col">Username</th>
                                            <th scope="col">Name</th>
                                            <th scope="col">Permission</th>
                                            <th scope="col">Operation</th>
                                        </tr>
                                    </thead>
                                    <tbody>
                                        {this.state.ps_student_list.map((student) => {
                                            const permission = parseInt(student.permission);
                                            const permission_str = permission < 1 ? "Student" : permission < 2 ? "Teacher" : "Administrator";
                                            const style = { overflowX: "auto", overflowY: "none", maxWidth: "15vw", textWrap: "nowrap", minWidth: "10vw" };
                                            return (
                                                <tr key={'student' + student.user_id}>
                                                    <td className='align-middle' style={style}>
                                                        {student.username}
                                                    </td>
                                                    <td className='align-middle' style={style}>
                                                        {student.name}
                                                    </td>
                                                    <td className='align-middle'>{permission_str}</td>
                                                    <td className='align-middle'>
                                                        <button className='btn btn-sm btn-outline-danger' disabled={this.state.is_loading || this.state.disable_edit} onClick={() => this.handleStudentRemove(student)}>
                                                            {this.handleLoadingRender()}
                                                            Drop
                                                        </button>
                                                    </td>
                                                </tr>
                                            );
                                        })}
                                    </tbody>
                                </table>
                            </div>
                        </div>
                    </div>
                </ContendCard>
            </React.Fragment>
        );
    }

    handleProblemSetInfoRender = () => {
        if (this.state.is_exam) {
            return (
                <div className="container-fluid card-body card">

                    <div className="row mb-md-2">
                        <div className="col-md-9">
                            <div className="row">
                                <div className="col-auto">
                                    <label htmlFor="psName" className="col-form-label">Name</label>
                                </div>
                                <div className="col">
                                    <input type="text" id="psName" className="form-control" onChange={(e) => { this.setState({ ps_name: e.target.value, ps_change: true }) }} disabled={this.state.is_loading || this.state.disable_edit} defaultValue={this.state.ps_name} />
                                </div>
                            </div>
                        </div>
                        <div className="col-md-3">
                            <div className="row">
                                <div className="col-auto">
                                    <label htmlFor="psAuthorName" className="col-form-label">Author</label>
                                </div>
                                <div className="col">
                                    <input type="text" id="psAuthorName" className="form-control" disabled value={this.state.ps_author_name} />
                                </div>
                            </div>
                        </div>
                    </div>

                    <div className="row mb-md-2">
                        <div className="col-md-4">
                            <div className="row">
                                <div className="col-auto">
                                    <label htmlFor="psStartTime" className="col-form-label">Start Time</label>
                                </div>
                                <div className="col">
                                    <input type="datetime-local" id="psStartTime" className="form-control" onChange={(e) => { this.setState({ ps_start_time: e.target.value, ps_change: true }) }} disabled={this.state.is_loading || this.state.disable_edit} defaultValue={this.state.ps_start_time} />
                                </div>
                            </div>
                        </div>
                        <div className="col-md-4">
                            <div className="row">
                                <div className="col-auto">
                                    <label htmlFor="psEndTime" className="col-form-label">End Time</label>
                                </div>
                                <div className="col">
                                    <input type="datetime-local" id="psEndTime" className="form-control" onChange={(e) => { this.setState({ ps_end_time: e.target.value, ps_change: true }) }} disabled={this.state.is_loading || this.state.disable_edit} defaultValue={this.state.ps_end_time} />
                                </div>
                            </div>
                        </div>
                        <div className="col-md-4">
                            <div className="row">
                                <div className="col-auto">
                                    <label htmlFor="duration" className="col-form-label">Duration</label>
                                </div>
                                <div className="col" style={{ minWidth: "77%" }}>
                                    <div className="input-group">
                                        <input type="number" id="duration" className="form-control" min={1} defaultValue={this.state.duration} onChange={(e) => { this.setState({ duration: e.target.value, ps_change: true }) }} disabled={this.state.is_loading || this.state.disable_edit} />
                                        <span className="input-group-text">minutes</span>
                                    </div>
                                </div>
                            </div>
                        </div>

                    </div>

                </div>
            );
        } else {
            return (
                <div className="container-fluid card card-body">

                    <div className="row mb-2">
                        <div className="col-md-9">
                            <div className="row">
                                <div className="col-auto">
                                    <label htmlFor="psName" className="col-form-label">Name</label>
                                </div>
                                <div className="col">
                                    <input type="text" id="psName" className="form-control" onChange={(e) => { this.setState({ ps_name: e.target.value, ps_change: true }) }} disabled={this.state.is_loading || this.state.disable_edit} defaultValue={this.state.ps_name} />
                                </div>
                            </div>
                        </div>
                        <div className="col-md-3">
                            <div className="row">
                                <div className="col-auto">
                                    <label htmlFor="psAuthorName" className="col-form-label">Author</label>
                                </div>
                                <div className="col">
                                    <input type="text" id="psAuthorName" className="form-control" disabled value={this.state.ps_author_name} />
                                </div>
                            </div>
                        </div>
                    </div>

                    <div className="row mb-2">
                        <div className="col-md-6">
                            <div className="row">
                                <div className="col-auto">
                                    <label htmlFor="psStartTime" className="col-form-label">Start Time</label>
                                </div>
                                <div className="col">
                                    <input type="datetime-local" id="psStartTime" className="form-control" onChange={(e) => { this.setState({ ps_start_time: e.target.value, ps_change: true }) }} disabled={this.state.is_loading || this.state.disable_edit} defaultValue={this.state.ps_start_time} />
                                </div>
                            </div>
                        </div>
                        <div className="col-md-6">
                            <div className="row">
                                <div className="col-auto">
                                    <label htmlFor="psEndTime" className="col-form-label">End Time</label>
                                </div>
                                <div className="col">
                                    <input type="datetime-local" id="psEndTime" className="form-control" onChange={(e) => { this.setState({ ps_end_time: e.target.value, ps_change: true }) }} disabled={this.state.is_loading || this.state.disable_edit} defaultValue={this.state.ps_end_time} />
                                </div>
                            </div>
                        </div>

                    </div>

                </div>
            );
        }
    }

    handlePreviewScoreRender = () => {
        if (this.props.permission > 1) {
            return (
                <button className='btn btn-outline-primary ms-2' onClick={() => this.props.navigate(`/problem_set/teacher_view/all_record/${this.props.params.problem_set_id}/`)}>
                    Transcript
                </button>
            );
        } else if (this.props.permission > 0 && parseInt(this.state.ps_author_id) === this.props.user_id) {
            return (
                <button className='btn btn-outline-primary ms-2' onClick={() => this.props.navigate(`/problem_set/teacher_view/all_record/${this.props.params.problem_set_id}/`)}>
                    Transcript
                </button>
            );
        }
    }

    handlePermissionRender = () => {
        if (this.props.permission > 0) {
            return (
                <div className="container">
                    <ContendCard>
                        <button className='btn btn-outline-primary' onClick={() => this.handleNavigateBack()}>
                            Back
                        </button>

                        {/* 查看成绩单 */}
                        {this.handlePreviewScoreRender()}

                        {/* 提交修改按钮在题目集基本信息下面 */}
                        {this.handleDeleteRender()}
                        <hr />
                        <div className='row mb-2'>
                            <div className='col-md-4'>
                                {this.handleTitleRender()}
                            </div>
                            <div className="col-md-8 d-flex justify-content-end align-items-center">
                                <span className='me-2' style={{ color: "red" }}>
                                    {this.state.error_message}
                                </span>
                                <button className='btn btn-success' onClick={() => this.handleSubmit()} disabled={this.state.is_loading || !this.state.ps_change || this.state.disable_edit}>
                                    {this.handleLoadingRender()}
                                    Update
                                </button>
                            </div>

                        </div>

                        {/* 题目集基本信息 */}
                        {this.handleProblemSetInfoRender()}

                        <hr />

                        {/* 试题管理 */}
                        {this.handleProblemRender()}
                        {/* 学生管理 */}
                        {this.handleStudentRender()}
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
                                        View a Problem Set ID={this.props.params.problem_set_id}
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
                                        View a Problem Set ID={this.props.params.problem_set_id}
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
        <SetPreview
            {...props}
            params={useParams()}
            navigate={useNavigate()}
        />
    )
);