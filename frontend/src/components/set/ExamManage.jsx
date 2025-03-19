import React, { Component } from 'react';
import { connect } from 'react-redux';
import { Link, NavLink, useNavigate } from 'react-router-dom';
import ContendCard from '../contents/ContentCard';

import $ from 'jquery';
import GET_INFO_TIMEOUT from '../config/GetInfoTimeoutConfig';
import BACKEND_ADDRESS_URL from "../config/BackendAddressURLConfig";
import PAGE_ITEM_LIMIT from '../config/PageItemLimit';

class ExamManage extends Component {
    state = {
        problem_set_list: [],

        new_ps_name: '',
        new_ps_start_time: '',
        new_ps_end_time: '',
        new_duration: '60',

        error_message: '',

        new_problem_set_id: '',
        new_id_received: false,

        page_number: 0, // 从0开始

        is_loading: false,
    }

    componentDidMount = () => {
        if (this.props.is_login) {
            this.handleGetAllExam();
        } else {
            setTimeout(this.handleGetAllExam, GET_INFO_TIMEOUT);
        }
    }

    handleGetAllExam = () => {
        // console.log("get all exam");
        const token = this.props.token;
        // console.log(token);
        this.setState({ is_loading: true });
        $.ajax({
            url: BACKEND_ADDRESS_URL + "/set_manage/exam/",
            type: "GET",
            headers: {
                Authorization: "Bearer " + token
            },
            success: (resp) => {
                // console.log(resp);
                this.setState({
                    problem_set_list: resp,
                    is_loading: false
                });
                if (resp.error_message) {
                    console.log(resp);
                }
            }
        });
    }

    handleJumpButtonRender = () => {
        if (this.state.new_id_received) {
            return (
                <button className='btn btn-outline-primary' data-bs-dismiss="modal" onClick={() => { this.props.navigate(`/set_manage/exam/${this.state.new_problem_set_id}`) }}>
                    View
                </button>
            );
        } else {
            return (
                <button type="button" className="btn btn-success" onClick={() => this.handleSubmit()} disabled={this.state.is_loading}>
                    {this.handleLoadingSpanRender()}
                    Create
                </button>
            );
        }
    }

    handleLoadingTrRender = () => {
        if (this.state.is_loading) {
            return (
                <tr>
                    <td>
                        <span className="spinner-border spinner-border-sm me-2" aria-hidden="true"></span>
                    </td>
                    <td>
                        <span className="spinner-border spinner-border-sm me-2" aria-hidden="true"></span>
                    </td>
                    <td>
                        <span className="spinner-border spinner-border-sm me-2" aria-hidden="true"></span>
                    </td>
                    <td>
                        <span className="spinner-border spinner-border-sm me-2" aria-hidden="true"></span>
                    </td>
                    <td>
                        <span className="spinner-border spinner-border-sm me-2" aria-hidden="true"></span>
                    </td>
                    <td>
                        <span className="spinner-border spinner-border-sm me-2" aria-hidden="true"></span>
                    </td>
                </tr>
            );
        }
    }

    handleLoadingSpanRender = () => {
        if (this.state.is_loading) {
            return (
                <span className="spinner-border spinner-border-sm me-2" aria-hidden="true"></span>
            );
        }
    }

    handleSubmit = () => {
        // console.log("create");
        // console.log(this.state);
        this.setState({
            error_message: '',
            is_loading: true
        });
        if (this.state.new_ps_name === '') {
            this.setState({
                error_message: 'The name cannot be empty',
                is_loading: false
            });
        } else if (this.state.new_ps_name.length > 100) {
            this.setState({
                error_message: 'The name cannot exceed 100 characters, current length: ' + this.state.new_ps_name.length,
                is_loading: false
            });
        } else if (this.state.new_ps_start_time === '') {
            this.setState({
                error_message: 'The start time cannot be empty',
                is_loading: false
            });
        } else if (this.state.new_ps_end_time === '') {
            this.setState({
                error_message: 'The end time cannot be empty',
                is_loading: false
            });
        } else if (Date.parse(this.state.new_ps_start_time) > Date.parse(this.state.new_ps_end_time)) {
            this.setState({
                error_message: 'The start time cannot be later than the end time',
                is_loading: false
            });
        } else if (this.state.new_duration === '') {
            this.setState({
                error_message: 'The duration cannot be empty',
                is_loading: false
            });
        } else if (parseInt(this.state.new_duration) <= 0) {
            this.setState({
                error_message: 'The duration must be a positive integer',
                is_loading: false
            });
        } else if (Date.parse(this.state.new_ps_start_time) + 1000 * 60 * parseInt(this.state.new_duration) > Date.parse(this.state.new_ps_end_time)) {
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
                type: "POST",
                data: {
                    psName: this.state.new_ps_name,
                    psStartTime: this.state.new_ps_start_time,
                    psEndTime: this.state.new_ps_end_time,
                    duration: this.state.new_duration,
                },
                headers: {
                    Authorization: "Bearer " + token
                },
                success: (resp) => {
                    // console.log(resp);
                    if (resp.error_message === 'success') {
                        this.setState({
                            error_message: "Exam created successfully",
                            new_problem_set_id: resp.problem_set_id,
                            new_id_received: true,
                            is_loading: true,  // 禁止重复提交
                        });
                        this.handleGetAllExam();
                    } else {
                        this.setState({
                            error_message: resp.error_message,
                            is_loading: false
                        });
                    }
                }
            });
        }
    }

    handleCreateModalRender = () => {
        return (
            <React.Fragment>
                {/* <!-- Button trigger modal --> */}
                <button type="button" className="btn btn-outline-primary" data-bs-toggle="modal" data-bs-target="#assignmentCreateModal">
                    Create
                </button>

                {/* <!-- Modal --> */}
                <div className="modal" id="assignmentCreateModal" tabIndex="-1" aria-labelledby="assignmentCreateModalLabel" aria-hidden="true">
                    <div className="modal-dialog modal-dialog-centered modal-lg">
                        <div className="modal-content">
                            <div className="modal-header">
                                <h1 className="modal-title fs-5" id="assignmentCreateModalLabel">
                                    Create
                                </h1>
                                <button type="button" className="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                            </div>
                            <div className="modal-body">
                                <div className="container-fluid">
                                    <div className="row mb-2">
                                        <div className="col-md-2">
                                            <label htmlFor="psName" className="col-form-label">Name</label>
                                        </div>
                                        <div className="col-md-10">
                                            <input type="text" id="psName" className="form-control" onChange={(e) => { this.setState({ new_ps_name: e.target.value }) }} />
                                        </div>
                                    </div>

                                    <div className="row mb-2">
                                        <div className="col-md-2">
                                            <label htmlFor="psAuthorName" className="col-form-label">Author</label>
                                        </div>
                                        <div className="col-md-10">
                                            <input type="text" id="psAuthorName" className="form-control" disabled value={this.props.name} />
                                        </div>
                                    </div>

                                    <div className="row mb-2">
                                        <div className="col-md-2">
                                            <label htmlFor="psStartTime" className="col-form-label">Start Time</label>
                                        </div>
                                        <div className="col-md-10">
                                            <input type="datetime-local" id="psStartTime" className="form-control" onChange={(e) => { this.setState({ new_ps_start_time: e.target.value }) }} />
                                        </div>
                                    </div>

                                    <div className="row mb-2">
                                        <div className="col-md-2">
                                            <label htmlFor="psEndTime" className="col-form-label">End Time</label>
                                        </div>
                                        <div className="col-md-10">
                                            <input type="datetime-local" id="psEndTime" className="form-control" onChange={(e) => { this.setState({ new_ps_end_time: e.target.value }) }} />
                                        </div>
                                    </div>

                                    <div className="row mb-2">
                                        <div className="col-md-2">
                                            <label htmlFor="duration" className="col-form-label">Duration</label>
                                        </div>
                                        <div className="col-md-10">
                                            <div className="input-group">
                                                <input type="number" id="duration" className="form-control" min={1} defaultValue={this.state.new_duration} onChange={(e) => { this.setState({ new_duration: e.target.value }) }} />
                                                <span className="input-group-text">minutes</span>
                                            </div>
                                        </div>
                                    </div>
                                </div>
                            </div>
                            <div className="modal-footer">
                                <span className='text-md-end' style={{ color: "red" }}>
                                    {this.state.error_message}
                                </span>
                                <button type="button" className="btn btn-secondary" data-bs-dismiss="modal">
                                    Close
                                </button>
                                {this.handleJumpButtonRender()}
                            </div>
                        </div>
                    </div>
                </div>
            </React.Fragment>
        );
    }

    renderTable = () => {
        const problem_set_list_display = [];
        for (let i = this.state.page_number * PAGE_ITEM_LIMIT; i < this.state.problem_set_list.length && i < (this.state.page_number + 1) * PAGE_ITEM_LIMIT; i++) {
            const problem_set = this.state.problem_set_list[i];
            problem_set_list_display.push({
                ...problem_set
            });
        }

        return (
            <table className="table table-striped">
                <thead>
                    <tr>
                        <th scope="col">ID</th>
                        <th scope="col">Name</th>
                        <th scope="col">Start Time</th>
                        <th scope="col">End Time</th>
                        <th scope="col">Duration</th>
                        <th scope="col">Author</th>
                    </tr>
                </thead>
                <tbody>
                    {this.handleLoadingTrRender()}
                    {problem_set_list_display.map((problem_set) => {
                        if (!problem_set.problem_set_id) {
                            return;
                        }
                        const ps_start_time = new Date(problem_set.ps_start_time);
                        const ps_end_time = new Date(problem_set.ps_end_time);
                        return (
                            <tr key={'ps' + problem_set.problem_set_id}>
                                <th scope='row'>{problem_set.problem_set_id}</th>
                                <td style={{ overflow: "hidden", textOverflow: "ellipsis", maxWidth: "20vw", minWidth: "20vw", textWrap: "nowrap" }}>
                                    <Link className='link-primary' to={`/set_manage/exam/${problem_set.problem_set_id}/`}>
                                        {problem_set.ps_name}
                                    </Link>
                                </td>
                                <td style={{ maxWidth: "7vw" }}>{ps_start_time.toLocaleString('zh-CN')}</td>
                                <td style={{ maxWidth: "7vw" }}>{ps_end_time.toLocaleString('zh-CN')}</td>
                                <td>{problem_set.duration} min</td>
                                <td>{problem_set.ps_author_name}</td>
                            </tr>
                        );
                    })}
                </tbody>
            </table>
        );
    }

    renderPreviousButton = () => {
        const page_number = this.state.page_number;
        if (this.state.page_number <= 0) {
            return (
                <button type="button" className="btn btn-outline-secondary" disabled>&laquo;</button>
                // <li className="page-item">
                //     <a className="page-link disabled" href="#" aria-label="Previous">
                //         <span aria-hidden="true">&laquo;</span>
                //     </a>
                // </li>
            );
        } else {
            return (
                <button type="button" className="btn btn-outline-primary" onClick={() => { this.setState({ page_number: page_number - 1 }) }}>&laquo;</button>
                // <li className="page-item">
                //     <a className="page-link" href="#" aria-label="Previous" onClick={() => { this.setState({ page_number: page_number - 1 }) }}>
                //         <span aria-hidden="true">&laquo;</span>
                //     </a>
                // </li>
            );
        }
    }

    renderNextButton = () => {
        const page_number = this.state.page_number;
        const total_page_count = Math.ceil(this.state.problem_set_list.length / PAGE_ITEM_LIMIT);
        // console.log(page_number, total_page_count);
        if (page_number >= total_page_count - 1) {
            return (
                <button type="button" className="btn btn-outline-secondary" disabled>&raquo;</button>
                // <li className="page-item">
                //     <a className="page-link disabled" href="#" aria-label="Next">
                //         <span aria-hidden="true">&raquo;</span>
                //     </a>
                // </li>
            );
        } else {
            return (
                <button type="button" className="btn btn-outline-primary" onClick={() => { this.setState({ page_number: page_number + 1 }) }}>&raquo;</button>
                // <li className="page-item">
                //     <a className="page-link" href="#" aria-label="Next" onClick={() => { this.setState({ page_number: page_number + 1 }) }}>
                //         <span aria-hidden="true">&raquo;</span>
                //     </a>
                // </li>
            );
        }
    }

    renderPageButtons = () => {
        const total_page_count = this.state.problem_set_list.length === 0 ? 1 : Math.ceil(this.state.problem_set_list.length / PAGE_ITEM_LIMIT);
        const page_number = this.state.page_number;
        const page_array = Array.from(Array(total_page_count), (_, x) => x);
        // console.log(page_array);
        return (
            <React.Fragment>
                {page_array.map((page) => {
                    if (page === page_number) {
                        return (
                            <button type="button" key={'page' + page} className="btn btn-primary" onClick={() => { this.setState({ page_number: page }) }}>
                                {page + 1}
                            </button>
                            // <li className="page-item active" key={'page' + page}>
                            //     <a className="page-link" href="#">
                            //         {page + 1}
                            //     </a>
                            // </li>
                        );
                    } else {
                        return (
                            <button type="button" key={'page' + page} className="btn btn-outline-primary" onClick={() => { this.setState({ page_number: page }) }}>
                                {page + 1}
                            </button>
                            // <li className="page-item" key={'page' + page}>
                            //     <a className="page-link" href="#" onClick={() => { this.setState({ page_number: page }) }}>
                            //         {page + 1}
                            //     </a>
                            // </li>
                        );
                    }
                })}
            </React.Fragment>
        );
    }

    handlePermissionRender = () => {
        if (this.props.permission > 0) {
            return (
                // real display of this page
                <div className="container">
                    <ContendCard>
                        {/* nav tabs */}
                        <ul className="nav nav-tabs justify-content-center">
                            <li className="nav-item">
                                <NavLink className="nav-link" to="/set_manage/assignment/">Assignments</NavLink>
                            </li>
                            <li className="nav-item">
                                <NavLink className="nav-link" to="/set_manage/exam/">Exams</NavLink>
                            </li>
                        </ul>

                        {/* new p/ button */}
                        <div className="row mt-3">
                            <div className="col-auto">
                                {this.handleCreateModalRender()}
                            </div>
                            <div className="col">
                                {/* button pagination */}
                                <div className='pagination justify-content-end flex-wrap mb-0'>
                                    <div className="btn-toolbar" role="toolbar" aria-label="Toolbar with button groups">
                                        <div className="btn-group me-2" role="group">
                                            {this.renderPreviousButton()}
                                            {this.renderPageButtons()}
                                            {this.renderNextButton()}
                                        </div>
                                    </div>
                                </div>
                                {/* render pagination */}
                                {/* <nav aria-label="Page navigation example">
                                    <ul className="pagination justify-content-end flex-wrap mb-0">
                                        {this.renderPreviousButton()}
                                        {this.renderPageButtons()}
                                        {this.renderNextButton()}
                                    </ul>
                                </nav> */}
                            </div>
                        </div>
                        <hr />

                        {/* exam table */}
                        {this.renderTable()}
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
                                    <h1 className="text-center">Exams</h1>
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
                                    <h1 className="text-center">Exams</h1>
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
        <ExamManage
            {...props}
            navigate={useNavigate()}
        />
    )
);